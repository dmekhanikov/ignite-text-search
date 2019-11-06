package mek.search.service;

import mek.search.model.Document;
import mek.search.model.Match;

import java.util.List;

public interface TextSearchService {
    String SERVICE_NAME = "search-service";
    int DEFAULT_LIMIT = 5;

    Document add(String text);
    List<Match> search(String query, int limit);
}
