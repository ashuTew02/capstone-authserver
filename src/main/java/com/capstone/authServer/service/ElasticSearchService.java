package com.capstone.authServer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
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
            e.printStackTrace();
        }
    }

    public SearchResultDTO<Finding> searchFindings(ScanToolType toolType, FindingSeverity severity, FindingState state, int page, int size) {
        SearchResponse<Finding> response;
        try {
            response = esClient.search(s -> s
                    .index("findings")
                    .query(q -> q.bool(buildBoolQuery(toolType, severity, state)))
                    .from(page * size)
                    .size(size),
                    Finding.class
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new SearchResultDTO<>(List.of(), 0, 0);
        }

        // Extract the list of findings
        List<Finding> findings = response.hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        // Extract total hits from the response
        // (This requires that Elasticsearch is returning an accurate total; 
        // otherwise .relation() might be "gte" and you'd have to handle that logic if needed)
        long totalHits = response.hits().total() != null
                ? response.hits().total().value()
                : 0;

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalHits / size);

        return new SearchResultDTO<>(findings, totalHits, totalPages);
    }


    private BoolQuery buildBoolQuery(ScanToolType toolType, FindingSeverity severity, FindingState state) {
        return BoolQuery.of(b -> {
            if (toolType != null) {
                b.must(m -> m.term(t -> t.field("toolType.keyword").value(toolType.name())));
            }
            if (severity != null) {
                b.must(m -> m.term(t -> t.field("severity.keyword").value(severity.name())));
            }
            if (state != null) {
                b.must(m -> m.term(t -> t.field("state.keyword").value(state.name())));
            }
            return b;
        });
    }
}