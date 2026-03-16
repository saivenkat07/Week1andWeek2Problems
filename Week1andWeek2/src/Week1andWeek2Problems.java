import java.util.*;

class TokenBucket {

    private int maxTokens;
    private double refillRate; // tokens per second
    private double tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens based on time passed
    private void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        double refillTokens = seconds * refillRate;

        tokens = Math.min(maxTokens, tokens + refillTokens);

        lastRefillTime = now;
    }

    // Try consuming a token
    public synchronized boolean allowRequest() {

        refill();

        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return (int) tokens;
    }
}

class RateLimiter {

    private HashMap<String, TokenBucket> clientBuckets = new HashMap<>();

    private int maxRequests = 1000;

    // 1000 requests per hour → convert to tokens per second
    private double refillRate = 1000.0 / 3600;

    public synchronized String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(maxRequests, refillRate));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {

            return "Request Allowed → Remaining tokens: " +
                    bucket.getRemainingTokens();

        } else {

            return "Rate Limit Exceeded → Try again later";
        }
    }

    public void getRateLimitStatus(String clientId) {

        if (!clientBuckets.containsKey(clientId)) {
            System.out.println("Client not found.");
            return;
        }

        TokenBucket bucket = clientBuckets.get(clientId);

        int remaining = bucket.getRemainingTokens();
        int used = maxRequests - remaining;

        System.out.println("Client: " + clientId);
        System.out.println("Used Requests: " + used);
        System.out.println("Remaining Requests: " + remaining);
        System.out.println("Limit: " + maxRequests);
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "client_XYZ";

        // Simulate API requests
        System.out.println(limiter.checkRateLimit(client));
        System.out.println(limiter.checkRateLimit(client));
        System.out.println(limiter.checkRateLimit(client));

        limiter.getRateLimitStatus(client);
    }
}
