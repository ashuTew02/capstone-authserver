package com.capstone.authServer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.exception.ElasticsearchOperationException;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient esClient;

    public ElasticSearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public void saveFinding(Finding finding) {
        try {
            esClient.index(i -> i.index("findings").id(finding.getId()).document(finding));
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error saving finding to Elasticsearch.", e);
        }
    }

    public SearchResultDTO<Finding> searchFindings(ScanToolType toolType,
                                                   FindingSeverity severity,
                                                   FindingState state,
                                                   int page,
                                                   int size) {
        try {
            // Use the builder to construct the query in a more readable manner
            var boolQuery = new FindingSearchQueryBuilder()
                                .withToolType(toolType)
                                .withSeverity(severity)
                                .withState(state)
                                .build();
                                SearchResponse<Finding> response = esClient.search(s -> s
                                .index("findings")
                                .query(q -> q.bool(boolQuery))
                                .sort(sort -> sort
                                    .field(f -> f
                                        // Use the appropriate field name based on your mapping
                                        .field("updatedAt")
                                        .order(SortOrder.Desc)
                                    )
                                )
                                .from(page * size)
                                .size(size),
                                Finding.class
                        );
            List<Finding> findings = response.hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());


            long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) totalHits / size);

            return new SearchResultDTO<>(findings, totalHits, totalPages);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ElasticsearchOperationException("Error searching findings in Elasticsearch.", e);
        }
    }

    public Finding getFindingById(String id) {

        try {
            var boolQuery = new FindingSearchQueryBuilder().withId(id).build();

            SearchResponse<Finding> response = esClient.search(s -> s
                    .index("findings")
                    .query(q -> q.bool(boolQuery))
                    .size(1),
                    Finding.class
            );

            List<Finding> findings = response.hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            if (findings.isEmpty()) {
                return null;
            } else {
                return findings.get(0);
            }
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Can't find the given finding.", e);
        }
    
    }
    


}
