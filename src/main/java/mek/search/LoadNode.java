package mek.search;

import mek.search.service.TextSearchService;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LoadNode {
    private static final int LOAD_BATCH_SIZE = 1000;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: \n\t java LoaderNode <text file>");
        }

        String path = args[0];

        Ignition.setClientMode(true);
        try (Ignite client = Ignition.start("config/ignite.xml");
             BufferedReader br = new BufferedReader(new FileReader(path))) {
            TextSearchService searchService =
                    client.services().serviceProxy("search-service", TextSearchService.class, false);

            List<String> lines = new ArrayList<>(LOAD_BATCH_SIZE);

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.isEmpty())
                    continue;

                lines.add(line);

                if (lines.size() == LOAD_BATCH_SIZE)
                    searchService.addAll(lines);
            }

            if (!lines.isEmpty())
                searchService.addAll(lines);
        }
    }
}
