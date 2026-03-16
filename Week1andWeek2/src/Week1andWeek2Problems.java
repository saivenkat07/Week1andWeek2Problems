import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class RealTimeAnalytics {

    // pageUrl -> visit count
    private HashMap<String, Integer> pageVisits = new HashMap<>();

    // pageUrl -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Process incoming event
    public synchronized void processEvent(PageEvent event) {

        // Count page visits
        pageVisits.put(event.url,
                pageVisits.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Count traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageVisits.entrySet()) {

            pq.add(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("===== REAL-TIME ANALYTICS DASHBOARD =====");
        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + visits + " views (" +
                    unique + " unique visitors)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = (count * 100.0) / total;

            System.out.println(source + ": " +
                    String.format("%.1f", percent) + "%");
        }

        System.out.println("=========================================");
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) throws Exception {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        // Simulated incoming events
        analytics.processEvent(new PageEvent("/news/election-update", "user101", "google"));
        analytics.processEvent(new PageEvent("/news/election-update", "user102", "direct"));
        analytics.processEvent(new PageEvent("/tech/ai-breakthrough", "user103", "facebook"));
        analytics.processEvent(new PageEvent("/sports/final-match", "user104", "google"));
        analytics.processEvent(new PageEvent("/news/election-update", "user105", "google"));
        analytics.processEvent(new PageEvent("/tech/ai-breakthrough", "user106", "direct"));
        analytics.processEvent(new PageEvent("/sports/final-match", "user107", "twitter"));
        analytics.processEvent(new PageEvent("/sports/final-match", "user108", "google"));

        // Dashboard refresh every 5 seconds
        analytics.getDashboard();
    }
}
