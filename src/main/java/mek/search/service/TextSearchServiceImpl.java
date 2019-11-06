package mek.search.service;

import mek.search.model.Document;
import mek.search.model.Match;
import mek.search.model.Request;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

import javax.cache.Cache;
import java.util.*;

import static org.springframework.util.StringUtils.countOccurrencesOf;

public class TextSearchServiceImpl implements TextSearchService, Service {
    private static final String CACHE_NAME = "documents";

    private static final long CACHE_LIFE_TIME = 60_000;

    private static final int CACHE_MAX_SIZE = 50_000;

    @IgniteInstanceResource
    private Ignite ignite;

    @LoggerResource
    private IgniteLogger log;

    private IgniteCache<UUID, Document> documentsCache;

    private final Map<Request, LinkedList<Match>> localRequestCache =
            Collections.synchronizedMap(new LinkedHashMap<>());

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
        Request req = new Request(qryText, limit, System.currentTimeMillis());

        LinkedList<Match> result = localRequestCache.get(req);

        if (result != null) {
            return result;
        }

        result = new LinkedList<>();

        Query<Cache.Entry<UUID, Document>> qry = new ScanQuery<>((k, v) -> v.getContent().contains(qryText));

        try (QueryCursor<Cache.Entry<UUID, Document>> cursor = documentsCache.query(qry)) {
            for (Cache.Entry<UUID, Document> e : cursor) {
                Document doc = e.getValue();
                int num = countOccurrencesOf(doc.getContent(), qryText);
                Match match = new Match(doc, num);
                insert(result, match, limit);
            }
        }

        if (localRequestCache.size() < CACHE_MAX_SIZE)
            localRequestCache.put(req, result);

        return result;
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
        documentsCache = ignite.getOrCreateCache(cacheConfiguration());

        log.info("Text search service has been initialized.");
    }

    private CacheConfiguration<UUID, Document> cacheConfiguration() {
        CacheConfiguration<UUID, Document> cacheCfg = new CacheConfiguration<>(CACHE_NAME);
        cacheCfg.setBackups(1);

        return cacheCfg;
    }

    @Override
    public void execute(ServiceContext serviceContext) throws InterruptedException {
        while (!serviceContext.isCancelled()) {
            synchronized (localRequestCache) {
                Iterator<Map.Entry<Request, LinkedList<Match>>> it = localRequestCache.entrySet().iterator();

                while (it.hasNext()) {
                    Request req = it.next().getKey();
                    if (req.getTimestamp() + CACHE_LIFE_TIME >= System.currentTimeMillis()) {
                        it.remove();
                    } else {
                        break;
                    }
                }
            }

            Thread.sleep(CACHE_LIFE_TIME);
        }
    }

    @Override
    public void cancel(ServiceContext serviceContext) {
        // No op.
    }
}
