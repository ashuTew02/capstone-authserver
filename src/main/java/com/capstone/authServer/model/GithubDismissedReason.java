package com.capstone.authServer.model;

public enum GithubDismissedReason {
    FALSE_POSITIVE("false positive"),
    WONT_FIX("won't fix"),
    USED_IN_TESTS("used in tests");

    private final String value;

    GithubDismissedReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
