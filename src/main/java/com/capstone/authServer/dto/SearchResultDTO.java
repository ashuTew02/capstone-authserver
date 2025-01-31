package com.capstone.authServer.dto;

import java.util.List;

public class SearchResultDTO<T> {
    private final List<T> items;
    private final long totalHits;
    private final int totalPages;

    public SearchResultDTO(List<T> items, long totalHits, int totalPages) {
        this.items = items;
        this.totalHits = totalHits;
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
