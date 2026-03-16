import java.util.*;

class TrieNode {
    HashMap<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}

class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query into Trie
    public void addQuery(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEndOfWord = true;

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Search prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, prefix, results);

        // Top 10 by frequency
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String query : results) {
            pq.add(query);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> topResults = new ArrayList<>(pq);
        topResults.sort((a, b) ->
                frequencyMap.get(b) - frequencyMap.get(a));

        return topResults;
    }

    // DFS to collect queries
    private void dfs(TrieNode node, String prefix, List<String> results) {

        if (node.isEndOfWord) {
            results.add(prefix);
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), prefix + c, results);
        }
    }

    // Update frequency when user searches
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }

    public int getFrequency(String query) {
        return frequencyMap.getOrDefault(query, 0);
    }
}

public class Week1_and_Week2_Problems {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript basics");
        system.addQuery("java download");
        system.addQuery("java interview questions");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");

        List<String> suggestions = system.search("jav");

        System.out.println("Search suggestions for 'jav':");

        int rank = 1;
        for (String s : suggestions) {
            System.out.println(rank + ". " + s +
                    " (" + system.getFrequency(s) + " searches)");
            rank++;
        }

        // Update frequency example
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nUpdated frequency for 'java 21 features': "
                + system.getFrequency("java 21 features"));
    }
}