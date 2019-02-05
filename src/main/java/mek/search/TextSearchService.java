package mek.search;

import mek.search.model.Document;
import mek.search.model.Match;

import java.util.List;

public interface TextSearchService {
    Document add(String text);
    List<Match> search(String query, int limit);
}
