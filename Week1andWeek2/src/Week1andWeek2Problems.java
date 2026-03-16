import java.util.*;

class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stockMap;

    // productId -> waiting list of users
    private HashMap<String, Queue<Integer>> waitingList;

    public FlashSaleInventoryManager() {
        stockMap = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product to inventory
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Purchase successful for user " + userId +
                    ". Remaining stock: " + (stock - 1);
        } else {

            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);

            return "Stock unavailable. User " + userId +
                    " added to waiting list at position #" + queue.size();
        }
    }

    // View waiting list
    public Queue<Integer> getWaitingList(String productId) {
        return waitingList.get(productId);
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        // Add product with limited stock
        manager.addProduct("PS5_CONSOLE", 3);

        // Check stock
        System.out.println("Stock check → " + manager.checkStock("PS5_CONSOLE") + " units available");

        // Purchase attempts
        System.out.println(manager.purchaseItem("PS5_CONSOLE", 1001));
        System.out.println(manager.purchaseItem("PS5_CONSOLE", 1002));
        System.out.println(manager.purchaseItem("PS5_CONSOLE", 1003));
        System.out.println(manager.purchaseItem("PS5_CONSOLE", 1004));
        System.out.println(manager.purchaseItem("PS5_CONSOLE", 1005));

        // Show waiting list
        System.out.println("Waiting List → " + manager.getWaitingList("PS5_CONSOLE"));
    }
}