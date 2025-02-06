package com.capstone.authServer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.exception.ElasticsearchOperationException;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient esClient;

    public ElasticSearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public void saveFinding(Finding finding) {
        try {
            esClient.index(i -> i
                .index("findings")
                .id(finding.getId())
                .document(finding)
            );
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error saving finding to Elasticsearch.", e);
        }
    }

    // CHANGED: Now each filter param is a List<Enum>, not a single Enum
    public SearchResultDTO<Finding> searchFindings(
            List<ScanToolType> toolTypes,
            List<FindingSeverity> severities,
            List<FindingState> states,
            int page,
            int size
    ) {
        try {
            // Build the bool query using the new multi-value methods
            var boolQuery = new FindingSearchQueryBuilder()
                    .withToolTypes(toolTypes)
                    .withSeverities(severities)
                    .withStates(states)
                    .build();

            // Perform the search
            SearchResponse<Finding> response = esClient.search(s -> s
                    .index("findings")
                    .query(q -> q.bool(boolQuery))
                    .sort(sort -> sort
                        .field(f -> f
                            .field("updatedAt") // or "updatedAt.keyword" if needed
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
            var boolQuery = new FindingSearchQueryBuilder()
                    .withId(id)
                    .build();

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

    public void updateFindingStateByFindingId(String id, FindingState state) {
        try {
            Finding finding = getFindingById(id);
            finding.setState(state);
            finding.setUpdatedAt(LocalDateTime.now().toString());
            IndexRequest<Finding> request = IndexRequest.of(builder ->
                builder.index("findings")
                    .id(finding.getId())
                    .document(finding)
            );

            IndexResponse response = esClient.index(request);
            System.out.println("Updated " + finding.getToolType() 
                                   + " job in ES findings index with _id: " 
                                   + response.id());
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error updating finding state in Elasticsearch.", e);
        }
    }
}
