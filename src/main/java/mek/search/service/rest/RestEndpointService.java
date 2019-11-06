package mek.search.service.rest;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.eclipse.jetty.server.Server;

public class RestEndpointService implements Service {
    @IgniteInstanceResource
    private Ignite ignite;

    @LoggerResource
    private IgniteLogger log;

    private Server server;

    @Override
    public void init(ServiceContext ctx) throws Exception {
        this.server = createServer();
        server.start();

        log.info("REST endpoint service has been initialized.");
    }

    @Override
    public void cancel(ServiceContext ctx) {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(ServiceContext ctx) {
    }

    private Server createServer() {
        Server server = new Server(8080);

        SearchJettyHandler handler = new SearchJettyHandler(ignite);
        server.setHandler(handler);

        return server;
    }

}
