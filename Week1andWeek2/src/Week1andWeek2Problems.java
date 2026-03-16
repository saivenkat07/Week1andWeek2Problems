import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class MultiLevelCache {

    // L1: in-memory LRU cache
    private LinkedHashMap<String, VideoData> l1Cache;
    private int l1Capacity = 10000;

    // L2: SSD-backed (simulated with HashMap pointing to file paths)
    private HashMap<String, String> l2Cache = new HashMap<>();
    private HashMap<String, Integer> l2AccessCount = new HashMap<>();
    private int l2Capacity = 100000;

    // L3: Database simulation
    private HashMap<String, VideoData> database;

    // Statistics
    private int l1Hits = 0, l1Miss = 0;
    private int l2Hits = 0, l2Miss = 0;
    private int l3Hits = 0, l3Miss = 0;
    private long l1Time = 0, l2Time = 0, l3Time = 0;

    public MultiLevelCache(HashMap<String, VideoData> database) {
        this.database = database;

        // LRU LinkedHashMap (access order)
        l1Cache = new LinkedHashMap<>(l1Capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > l1Capacity;
            }
        };
    }

    public VideoData getVideo(String videoId) {

        long start = System.nanoTime();

        // L1 cache
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            l1Time += (System.nanoTime() - start) / 1000000; // ms
            return l1Cache.get(videoId);
        } else {
            l1Miss++;
        }

        // L2 cache
        start = System.nanoTime();
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            l2Time += (System.nanoTime() - start) / 1000000;

            // Promote to L1 if access count > threshold
            int count = l2AccessCount.getOrDefault(videoId, 0) + 1;
            l2AccessCount.put(videoId, count);
            if (count >= 3) { // threshold
                l1Cache.put(videoId, new VideoData(videoId, "Content from SSD"));
            }

            return new VideoData(videoId, "Content from SSD");
        } else {
            l2Miss++;
        }

        // L3 Database
        start = System.nanoTime();
        if (database.containsKey(videoId)) {
            l3Hits++;
            l3Time += (System.nanoTime() - start) / 1000000;

            // Add to L2
            if (l2Cache.size() >= l2Capacity) {
                // Simple eviction (random) for demonstration
                Iterator<String> it = l2Cache.keySet().iterator();
                if (it.hasNext()) {
                    String evict = it.next();
                    it.remove();
                    l2AccessCount.remove(evict);
                }
            }
            l2Cache.put(videoId, "SSD_Path/" + videoId);
            l2AccessCount.put(videoId, 1);

            return database.get(videoId);
        } else {
            l3Miss++;
            return null;
        }
    }

    public void getStatistics() {
        int l1Total = l1Hits + l1Miss;
        int l2Total = l2Hits + l2Miss;
        int l3Total = l3Hits + l3Miss;

        int overallHits = l1Hits + l2Hits + l3Hits;
        int overallTotal = l1Total + l2Total + l3Total;

        System.out.println("=== Multi-Level Cache Statistics ===");
        System.out.println("L1: Hit Rate " + String.format("%.2f", 100.0 * l1Hits / l1Total) + "%, Avg Time: " +
                String.format("%.2f", l1Time * 1.0 / l1Total) + " ms");
        System.out.println("L2: Hit Rate " + String.format("%.2f", 100.0 * l2Hits / l2Total) + "%, Avg Time: " +
                String.format("%.2f", l2Time * 1.0 / l2Total) + " ms");
        System.out.println("L3: Hit Rate " + String.format("%.2f", 100.0 * l3Hits / l3Total) + "%, Avg Time: " +
                String.format("%.2f", l3Time * 1.0 / l3Total) + " ms");
        System.out.println("Overall Hit Rate: " + String.format("%.2f", 100.0 * overallHits / overallTotal) + "%");
        System.out.println("====================================");
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        // Simulated database
        HashMap<String, VideoData> db = new HashMap<>();
        db.put("video_123", new VideoData("video_123", "Video Content 123"));
        db.put("video_999", new VideoData("video_999", "Video Content 999"));

        MultiLevelCache cache = new MultiLevelCache(db);

        System.out.println("Request 1: video_123");
        cache.getVideo("video_123");

        System.out.println("Request 2: video_123");
        cache.getVideo("video_123");

        System.out.println("Request 3: video_999");
        cache.getVideo("video_999");

        System.out.println("\nCache Statistics:");
        cache.getStatistics();
    }
}
