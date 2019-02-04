package mek.search.model;

import java.util.Objects;
import java.util.UUID;

public class Document {
    private final UUID id;
    private final String content;
    private final long timeAdded;

    public Document(UUID id, String content, long timeAdded) {
        this.id = id;
        this.content = content;
        this.timeAdded = timeAdded;
    }

    public String getContent() {
        return content;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id.equals(document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
