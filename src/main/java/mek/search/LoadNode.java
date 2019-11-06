package mek.search;

import mek.search.service.TextSearchService;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import java.io.BufferedReader;
import java.io.FileReader;

public class LoadNode {
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

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.isEmpty())
                    continue;

                searchService.add(line);
            }
        }
    }
}
