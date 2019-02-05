package mek.search.model;

import org.jetbrains.annotations.NotNull;

public class Match implements Comparable<Match> {
    private final Document doc;
    private final int num;

    public Match(Document doc, int num) {
        this.doc = doc;
        this.num = num;
    }

    public Document getDocument() {
        return doc;
    }

    public int getNumberOfOccurrences() {
        return num;
    }

    @Override
    public int compareTo(@NotNull Match o) {
        if (num != o.num) {
            return Integer.compare(num, o.num);
        } else {
            if (doc.getTimeAdded() != o.doc.getTimeAdded()) {
                return Long.compare(doc.getTimeAdded(), o.doc.getTimeAdded());
            } else {
                return doc.getId().compareTo(o.doc.getId());
            }
        }
    }
}
