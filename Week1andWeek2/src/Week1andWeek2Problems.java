import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private int capacity;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED for " + domain);
            }
        }

        misses++;

        // Query upstream DNS
        String ip = queryUpstreamDNS(domain);

        // Store with TTL
        cache.put(domain, new DNSEntry(domain, ip, 10));

        return "Cache MISS → Upstream Query → " + ip;
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "192.168." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class Week1_and_Week2_Problems {

    public static void main(String[] args) throws Exception {

        DNSCache dnsCache = new DNSCache(3);

        System.out.println(dnsCache.resolve("openai.com"));
        System.out.println(dnsCache.resolve("github.com"));
        System.out.println(dnsCache.resolve("openai.com"));
        System.out.println(dnsCache.resolve("stackoverflow.com"));

        Thread.sleep(11000); // wait for TTL expiry

        System.out.println(dnsCache.resolve("openai.com"));

        dnsCache.getCacheStats();
    }
}