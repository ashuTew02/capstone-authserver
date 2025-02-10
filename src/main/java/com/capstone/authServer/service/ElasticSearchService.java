package com.capstone.authServer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import io.jsonwebtoken.io.IOException;

import com.capstone.authServer.dto.SearchResultDTO;
import com.capstone.authServer.exception.ElasticsearchOperationException;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
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

    // ============================================================
    //               MAIN SEARCH (Findings List)
    // ============================================================
    public SearchResultDTO<Finding> searchFindings(
            List<ScanToolType> toolTypes,
            List<FindingSeverity> severities,
            List<FindingState> states,
            int page,
            int size
    ) {
        try {
            var boolQuery = new FindingSearchQueryBuilder()
                    .withToolTypes(toolTypes)
                    .withSeverities(severities)
                    .withStates(states)
                    .build();

            SearchResponse<Finding> response = esClient.search(s -> s
                    .index("findings")
                    .query(q -> q.bool(boolQuery))
                    .sort(sort -> sort
                        .field(f -> f
                            .field("updatedAt")
                            .order(SortOrder.Desc)
                        )
                    )
                    .from(page * size)
                    .size(size),
                Finding.class
            );

            List<Finding> findings = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            long totalHits = (response.hits().total() != null)
                    ? response.hits().total().value()
                    : 0;
            int totalPages = (int) Math.ceil((double) totalHits / size);

            return new SearchResultDTO<>(findings, totalHits, totalPages);

        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error searching findings in Elasticsearch.", e);
        }
    }

    // ============================================================
    //                 GET SINGLE FINDING BY ID
    // ============================================================
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

            List<Finding> findings = response.hits().hits().stream()
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

    // ============================================================
    //            UPDATE A FINDING'S STATE (PATCH)
    // ============================================================
    public void updateFindingStateByFindingId(String id, FindingState state) {
        try {
            Finding finding = getFindingById(id);
            if (finding == null) {
                throw new ElasticsearchOperationException("Finding not found with ID: " + id);
            }
            finding.setState(state);
            finding.setUpdatedAt(LocalDateTime.now().toString());

            esClient.index(i -> i
                .index("findings")
                .id(finding.getId())
                .document(finding)
            );
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error updating finding state in Elasticsearch.", e);
        }
    }

    // ============================================================
    //                 DASHBOARD AGGREGATIONS
    // ============================================================

    /**
     * Returns the distribution of findings by toolType (CODE_SCAN, DEPENDABOT, SECRET_SCAN).
     * -> Terms aggregator on "toolType.keyword" (string).
     */
    public Map<String, Long> getToolDistribution() {
        try {
            SearchResponse<Void> response = esClient.search(s -> s
                    .index("findings")
                    .size(0)
                    .aggregations("toolAgg", a -> a
                        .terms(t -> t.field("toolType.keyword").size(10))
                    ),
                Void.class
            );
    
            var agg = response.aggregations().get("toolAgg").sterms();
            Map<String, Long> result = new LinkedHashMap<>();
            for (StringTermsBucket bucket : agg.buckets().array()) {
                // Convert FieldValue to String using stringValue()
                String key = bucket.key().stringValue();
                long docCount = bucket.docCount();
                result.put(key, docCount);
            }
            return result;
    
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error aggregating tool distribution.", e);
        }
    }
    

    /**
     * Returns distribution by severity (CRITICAL, HIGH, etc.), optionally filtered by tool.
     * -> Terms aggregator on "severity.keyword" (string).
     */
    public Map<String, Long> getSeverityDistribution(List<String> tool) {
        try {
            var qb = new FindingSearchQueryBuilder();
            if (tool != null && !tool.isEmpty()) {
                List<ScanToolType> toolTypes = new ArrayList<>();
                for (String t : tool) {
                    try {
                        toolTypes.add(ScanToolType.valueOf(t));
                    } catch (Exception ex) {
                        // ignore invalid
                    }
                }
                qb.withToolTypes(toolTypes);
            }
    
            var boolQuery = qb.build();
    
            SearchResponse<Void> response = esClient.search(s -> s
                    .index("findings")
                    .size(0)
                    .query(q -> q.bool(boolQuery))
                    .aggregations("severityAgg", a -> a
                        .terms(t -> t.field("severity.keyword").size(10))
                    ),
                Void.class
            );
    
            var agg = response.aggregations().get("severityAgg").sterms();
            Map<String, Long> result = new LinkedHashMap<>();
            for (StringTermsBucket bucket : agg.buckets().array()) {
                // Convert the FieldValue to String
                String key = bucket.key().stringValue();
                long docCount = bucket.docCount();
                result.put(key, docCount);
            }
            return result;
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error aggregating severity distribution.", e);
        }
    }
    

    /**
     * Returns distribution by state (OPEN, FIXED, SUPPRESSED, etc.), optionally filtered by tool.
     * -> Terms aggregator on "state.keyword" (string).
     */
    public Map<String, Long> getStateDistribution(List<String> tool) {
        try {
            var qb = new FindingSearchQueryBuilder();
            if (tool != null && !tool.isEmpty()) {
                List<ScanToolType> toolTypes = new ArrayList<>();
                for (String t : tool) {
                    try {
                        toolTypes.add(ScanToolType.valueOf(t));
                    } catch (Exception ignored) {}
                }
                qb.withToolTypes(toolTypes);
            }
            var boolQuery = qb.build();
    
            SearchResponse<Void> response = esClient.search(s -> s
                    .index("findings")
                    .size(0)
                    .query(q -> q.bool(boolQuery))
                    .aggregations("stateAgg", a -> a
                        .terms(t -> t.field("state.keyword").size(10))
                    ),
                Void.class
            );
    
            var agg = response.aggregations().get("stateAgg").sterms();
            Map<String, Long> result = new LinkedHashMap<>();
            for (StringTermsBucket bucket : agg.buckets().array()) {
                String key = bucket.key().stringValue();
                long docCount = bucket.docCount();
                result.put(key, docCount);
            }
            return result;
        } catch (Exception e) {
            throw new ElasticsearchOperationException("Error aggregating state distribution.", e);
        }
    }
    

    /**
     * Returns distribution of findings by CVSS score bins (0-1,1-2,...9-10).
     * -> "histogram" aggregator on numeric field "cvss".
     * @throws java.io.IOException 
     * @throws ElasticsearchException 
     */
public List<Map<String, Object>> getCvssDistribution(List<String> tool) throws ElasticsearchException, java.io.IOException{
        /*
         * We'll do a histogram aggregator with a script that:
         * 1) Checks doc['cvss.keyword'].size() != 0
         * 2) Tries Double.parseDouble(...)
         * 3) If missing/invalid => return -1
         */
        logger.info("AFTER AGGREGATION: ");

        Aggregation histAgg = Aggregation.of(a -> a
            .histogram(h -> h
                .script(script -> script
                    .lang("painless")
                    .source("""
    if (doc['cvss.keyword'].size() != 0) {
      def str = doc['cvss.keyword'].value;
      try {
        double val = Double.parseDouble(str);
        return val;
      } catch (Exception e) {
        return -1.0; // fallback if parse fails
      }
    } else {
      return -1.0; // fallback if field missing
    }
    """)
                )
                .interval(1.0)
                .extendedBounds(b -> b
                    .min(0.0)
                    .max(10.0)
                )
            )
        );
        SearchRequest sr = SearchRequest.of(s -> s
            .index("findings")
            .size(0)
            .aggregations("cvssHist", histAgg)
        );
        SearchResponse<Void> resp = esClient.search(sr, Void.class);
        var histogramAgg = resp.aggregations().get("cvssHist").histogram();
        var buckets = histogramAgg.buckets().array();
        List<Map<String, Object>> results = new ArrayList<>();
        for (HistogramBucket bucket : buckets) {
            double key = bucket.key();    // e.g. 0.0,1.0,2.0,...
            long docCount = bucket.docCount();
            // If key == -1 => those are docs we couldn't parse or missing.
            // We can skip them or keep them. If skipping:
            if (key < 0) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("bucket", key);
            item.put("count", docCount);
            results.add(item);
        }
        return results;
    }
    
}
