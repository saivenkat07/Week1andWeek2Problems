import java.util.*;

class UsernameChecker {

    private HashMap<String, Integer> usernameMap;

    private HashMap<String, Integer> attemptFrequency;

    public UsernameChecker() {
        usernameMap = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }


    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
    }

    public boolean checkAvailability(String username) {


        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }


    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;

            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String modified = username.replace("_", ".");
        if (!usernameMap.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (String username : attemptFrequency.keySet()) {

            int count = attemptFrequency.get(username);

            if (count > max) {
                max = count;
                result = username;
            }
        }

        return result + " (" + max + " attempts)";
    }
}

public class Week1andWeek2Problems {

    public static void main(String[] args) {

        UsernameChecker system = new UsernameChecker();

        system.registerUser("Sai", 101);
        system.registerUser("admin", 1);
        system.registerUser("Venkat", 102);

        System.out.println("checkAvailability(\"Sai\") → "
                + system.checkAvailability("Sai"));

        System.out.println("checkAvailability(\"Venkat\") → "
                + system.checkAvailability("Venkat"));

        System.out.println("suggestAlternatives(\"Sai\") → "
                + system.suggestAlternatives("Sai"));

        for (int i = 0; i < 10543; i++) {
            system.checkAvailability("admin");
        }

        System.out.println("getMostAttempted() → "
                + system.getMostAttempted());
    }
}
