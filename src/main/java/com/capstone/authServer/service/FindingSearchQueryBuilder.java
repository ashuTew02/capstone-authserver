package com.capstone.authServer.service;

import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;

public class FindingSearchQueryBuilder {

    private final BoolQuery.Builder builder;

    public FindingSearchQueryBuilder() {
        this.builder = new BoolQuery.Builder();
    }

    public FindingSearchQueryBuilder withToolType(ScanToolType toolType) {
        if (toolType != null) {
            builder.must(m -> m.term(t -> t.field("toolType.keyword").value(toolType.name())));
        }
        return this;
    }

    public FindingSearchQueryBuilder withSeverity(FindingSeverity severity) {
        if (severity != null) {
            builder.must(m -> m.term(t -> t.field("severity.keyword").value(severity.name())));
        }
        return this;
    }

    public FindingSearchQueryBuilder withState(FindingState state) {
        if (state != null) {
            builder.must(m -> m.term(t -> t.field("state.keyword").value(state.name())));
        }
        return this;
    }

    public FindingSearchQueryBuilder withId(String id) {
        if (id != null) {
            builder.must(m -> m.term(t -> t.field("id.keyword").value(id)));
        }
        return this;
    }

    public BoolQuery build() {
        return builder.build();
    }
}
