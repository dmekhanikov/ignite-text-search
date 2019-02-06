package mek.search.model;

import java.util.Objects;

public class Request {
    private final String query;
    private final int limit;
    private final long timestamp;

    public Request(String query, int limit, long timestamp) {
        this.query = query;
        this.limit = limit;
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public int getLimit() {
        return limit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return limit == request.limit &&
                query.equals(request.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, limit);
    }
}
