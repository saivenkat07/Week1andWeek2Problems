import java.util.*;

class Transaction {
    int id;
    double amount;
    String merchant;
    String account;
    long timestamp; // epoch ms

    public Transaction(int id, double amount, String merchant, String account, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String toString() {
        return "{id:" + id + ", amount:" + amount + ", merchant:" + merchant + ", account:" + account + "}";
    }
}

class TransactionAnalyzer {

    private List<Transaction> transactions;

    public TransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Classic Two-Sum
    public List<List<Transaction>> findTwoSum(double target) {
        Map<Double, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }
            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum within time window (in ms)
    public List<List<Transaction>> findTwoSumWithWindow(double target, long windowMs) {
        Map<Double, List<Transaction>> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction c : map.get(complement)) {
                    if (Math.abs(t.timestamp - c.timestamp) <= windowMs) {
                        result.add(Arrays.asList(c, t));
                    }
                }
            }
            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }

        return result;
    }

    // K-Sum recursive
    public List<List<Transaction>> findKSum(int k, double target) {
        List<List<Transaction>> result = new ArrayList<>();
        findKSumHelper(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void findKSumHelper(List<Transaction> list, int k, double target, int start,
                                List<Transaction> temp, List<List<Transaction>> result) {
        if (k == 2) {
            Map<Double, Transaction> map = new HashMap<>();
            for (int i = start; i < list.size(); i++) {
                Transaction t = list.get(i);
                double complement = target - t.amount;
                if (map.containsKey(complement)) {
                    List<Transaction> pair = new ArrayList<>(temp);
                    pair.add(map.get(complement));
                    pair.add(t);
                    result.add(pair);
                }
                map.put(t.amount, t);
            }
        } else {
            for (int i = start; i < list.size(); i++) {
                temp.add(list.get(i));
                findKSumHelper(list, k - 1, target - list.get(i).amount, i + 1, temp, result);
                temp.remove(temp.size() - 1);
            }
        }
    }

    // Detect duplicates: same amount, same merchant, different accounts
    public List<Map<String, Object>> detectDuplicates() {
        Map<String, List<String>> map = new HashMap<>();
        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t.account);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : map.keySet()) {
            List<String> accounts = map.get(key);
            Set<String> uniqueAccounts = new HashSet<>(accounts);
            if (uniqueAccounts.size() > 1) {
                String[] parts = key.split("\\|");
                Map<String, Object> entry = new HashMap<>();
                entry.put("amount", Double.parseDouble(parts[0]));
                entry.put("merchant", parts[1]);
                entry.put("accounts", uniqueAccounts);
                result.add(entry);
            }
        }

        return result;
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        long now = System.currentTimeMillis();

        List<Transaction> txns = Arrays.asList(
                new Transaction(1, 500, "Store A", "acc1", now - 3600_000),
                new Transaction(2, 300, "Store B", "acc2", now - 1800_000),
                new Transaction(3, 200, "Store C", "acc3", now - 1200_000),
                new Transaction(4, 500, "Store A", "acc2", now - 900_000)
        );

        TransactionAnalyzer analyzer = new TransactionAnalyzer(txns);

        System.out.println("=== Classic Two-Sum (target=500) ===");
        List<List<Transaction>> twoSum = analyzer.findTwoSum(500);
        for (List<Transaction> pair : twoSum) {
            System.out.println(pair);
        }

        System.out.println("\n=== Two-Sum within 1 hour ===");
        List<List<Transaction>> twoSumWindow = analyzer.findTwoSumWithWindow(500, 3600_000);
        for (List<Transaction> pair : twoSumWindow) {
            System.out.println(pair);
        }

        System.out.println("\n=== K-Sum (k=3, target=1000) ===");
        List<List<Transaction>> kSum = analyzer.findKSum(3, 1000);
        for (List<Transaction> group : kSum) {
            System.out.println(group);
        }

        System.out.println("\n=== Duplicate Detection ===");
        List<Map<String, Object>> duplicates = analyzer.detectDuplicates();
        for (Map<String, Object> entry : duplicates) {
            System.out.println(entry);
        }
    }
}
