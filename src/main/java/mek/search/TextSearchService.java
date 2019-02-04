package mek.search;

import mek.search.model.Document;
import mek.search.model.Occurrence;

import java.util.List;

public interface TextSearchService {
    Document add(String text);
    List<Occurrence> search(String query, int limit);
}
