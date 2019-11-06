package mek.search;

import mek.search.service.TextSearchService;
import mek.search.service.rest.RestEndpointService;
import mek.search.service.TextSearchServiceImpl;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class DeployNode {
    public static void main(String[] args) {
        Ignition.setClientMode(true);
        try (Ignite client = Ignition.start("config/ignite.xml")) {
            client.services().deployNodeSingleton(TextSearchService.SERVICE_NAME, new TextSearchServiceImpl());
            client.services().deployClusterSingleton("rest-service", new RestEndpointService());
        }
    }
}
