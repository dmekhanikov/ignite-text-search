package mek.search.service.rest;

import mek.search.model.Match;

import java.util.List;

public class SearchResult {
    private final List<Match> matches;

    public SearchResult(List<Match> matches) {
        this.matches = matches;
    }

    public List<Match> getMatches() {
        return matches;
    }
}
