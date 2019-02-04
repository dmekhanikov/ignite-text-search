package mek.search.model;

import org.jetbrains.annotations.NotNull;

public class Occurrence implements Comparable<Occurrence> {
    private final Document doc;
    private final int num;

    public Occurrence(Document doc, int num) {
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
    public int compareTo(@NotNull Occurrence o) {
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
