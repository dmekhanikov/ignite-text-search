package mek.search;

import mek.search.model.Document;
import mek.search.model.Match;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

import javax.cache.Cache;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class TextSearchServiceImpl implements TextSearchService, Service {
    private static final String CACHE_NAME = "documents";

    @IgniteInstanceResource
    private Ignite ignite;

    private IgniteCache<UUID, Document> documentsCache;

    @Override
    public Document add(String text) {
        UUID id = UUID.randomUUID();

        Document doc = new Document(
                id,
                text,
                System.currentTimeMillis());

        documentsCache.put(id, doc);

        return doc;
    }

    @Override
    public List<Match> search(String qryText, int limit) {
        LinkedList<Match> result = new LinkedList<>();

        Query<Cache.Entry<UUID, Document>> qry = new ScanQuery<>((k, v) -> v.getContent().contains(qryText));

        try (QueryCursor<Cache.Entry<UUID, Document>> cursor = documentsCache.query(qry)) {
            for (Cache.Entry<UUID, Document> e : cursor) {
                Document doc = e.getValue();
                int num = countOccurrencesOf(doc.getContent(), qryText);
                Match match = new Match(doc, num);
                insert(result, match, limit);
            }
        }

        return result;
    }

    private int countOccurrencesOf(String str, String sub) {
        if (str.isEmpty() || sub.isEmpty()) {
            return 0;
        }

        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    private void insert(LinkedList<Match> list, Match oc, int limit) {
        ListIterator<Match> it = list.listIterator();
        boolean found = false;
        for (int i = 0; i < limit && it.hasNext(); i++) {
            Match oc1 = it.next();
            if (oc1.compareTo(oc) < 0) {
                if (i == 0) {
                    list.addFirst(oc);
                } else {
                    it.previous();
                    it.add(oc);
                }
                found = true;
                break;
            }
        }
        if (!found && list.size() < limit) {
            list.addLast(oc);
        } else if (list.size() > limit) {
            list.removeLast();
        }
    }

    @Override
    public void init(ServiceContext serviceContext) {
        documentsCache = ignite.getOrCreateCache(CACHE_NAME);
    }

    @Override
    public void cancel(ServiceContext serviceContext) {
        // No op.
    }

    @Override
    public void execute(ServiceContext serviceContext) {
        // No op.
    }
}
