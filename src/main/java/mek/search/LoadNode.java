package mek.search;

import mek.search.model.Match;
import mek.search.service.TextSearchService;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import java.util.List;

public class LoadNode {
    public static void main(String[] args) {
        Ignition.setClientMode(true);
        try (Ignite client = Ignition.start("config/ignite.xml")) {

            TextSearchService searchService =
                    client.services().serviceProxy("search-service", TextSearchService.class, false);

            searchService.add("Lorem ipsum dolor sit amet");
            searchService.add("consectetur adipiscing elit");
            searchService.add("Pellentesque a suscipit elit");
            searchService.add("ut congue nisi.");
            searchService.add("Etiam ullamcorper mi neque,");
            searchService.add("eget condimentum purus bibendum et.");
            searchService.add("Nunc pharetra, lorem eget rhoncus tempus");
            searchService.add("odio odio pulvinar dolor");
            searchService.add("et viverra libero libero quis libero.");
            searchService.add("Nam vel leo condimentum, luctus nulla nec,");
            searchService.add("commodo augue. Quisque nec enim laoreet");
            searchService.add("ultrices dui porta, euismod felis");

            List<Match> matches = searchService.search("et", 3);
            for (Match match : matches) {
                System.out.println("Match: " + match.getDocument().getContent() +
                        "; num: " + match.getNumberOfOccurrences());
            }
        }
    }
}
