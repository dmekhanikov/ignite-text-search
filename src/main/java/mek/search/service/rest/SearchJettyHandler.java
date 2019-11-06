package mek.search.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import mek.search.model.Match;
import mek.search.service.TextSearchService;
import org.apache.ignite.Ignite;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

class SearchJettyHandler extends AbstractHandler {
    private final Ignite ignite;
    private final ObjectMapper jacksonMapper;

    SearchJettyHandler(Ignite ignite) {
        this.ignite = ignite;

        this.jacksonMapper = new ObjectMapper();
        jacksonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        List<Match> matches;

        try {
            matches = search(baseRequest);
        } catch (IllegalArgumentException ex) {
            badRequest(baseRequest, response, ex.getMessage());
            return;
        }

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println(jacksonMapper.writeValueAsString(new SearchResult(matches)));

        baseRequest.setHandled(true);
    }

    private List<Match> search(Request baseRequest) {
        String query = baseRequest.getParameter("query");

        if (query == null) {
            throw new IllegalArgumentException("\"query\" parameter is not specified.");
        }

        int limit;

        String limitStr = baseRequest.getParameter("limit");
        if (limitStr == null)
            limit = TextSearchService.DEFAULT_LIMIT;
        else {
            try {
                limit = Integer.parseInt(limitStr);

            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("\"limit\" parameter should be a number. " + ex.getMessage(), ex);
            }
        }

        TextSearchService searchService = ignite.services().serviceProxy(TextSearchService.SERVICE_NAME,
                TextSearchService.class, false);

        return searchService.search(query, limit);
    }

    private void badRequest(Request baseRequest, HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        PrintWriter out = response.getWriter();
        out.println(message);

        baseRequest.setHandled(true);
    }
}
