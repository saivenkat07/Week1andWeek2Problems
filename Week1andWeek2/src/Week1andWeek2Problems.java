import java.util.*;

class ParkingSpot {
    enum Status { EMPTY, OCCUPIED, DELETED }

    Status status = Status.EMPTY;
    String licensePlate = null;
    long entryTime = 0; // timestamp in ms
}

class ParkingLot {

    private ParkingSpot[] spots;
    private int capacity;

    private int totalProbes = 0;
    private int totalParked = 0;
    private int maxOccupancy = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new ParkingSpot();
        }
    }

    // Custom hash function for license plate
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park a vehicle
    public String parkVehicle(String licensePlate) {

        int preferred = hash(licensePlate);
        int probes = 0;

        for (int i = 0; i < capacity; i++) {

            int spotIndex = (preferred + i) % capacity;
            ParkingSpot spot = spots[spotIndex];

            if (spot.status == ParkingSpot.Status.EMPTY ||
                    spot.status == ParkingSpot.Status.DELETED) {

                spot.status = ParkingSpot.Status.OCCUPIED;
                spot.licensePlate = licensePlate;
                spot.entryTime = System.currentTimeMillis();

                totalProbes += probes;
                totalParked++;
                maxOccupancy = Math.max(maxOccupancy, totalParked);

                return "Vehicle " + licensePlate +
                        " assigned Spot #" + spotIndex +
                        " (" + probes + " probe(s))";
            }

            probes++;
        }

        return "Parking Full! Could not assign spot.";
    }

    // Exit vehicle
    public String exitVehicle(String licensePlate) {

        int preferred = hash(licensePlate);

        for (int i = 0; i < capacity; i++) {

            int spotIndex = (preferred + i) % capacity;
            ParkingSpot spot = spots[spotIndex];

            if (spot.status == ParkingSpot.Status.OCCUPIED &&
                    spot.licensePlate.equals(licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMs = exitTime - spot.entryTime;
                double hours = durationMs / 3600000.0;
                double fee = hours * 5; // $5/hour

                spot.status = ParkingSpot.Status.DELETED;
                spot.licensePlate = null;
                spot.entryTime = 0;

                totalParked--;

                return "Vehicle " + licensePlate +
                        " exited Spot #" + spotIndex +
                        ", Duration: " + String.format("%.2f", hours) +
                        "h, Fee: $" + String.format("%.2f", fee);
            }
        }

        return "Vehicle " + licensePlate + " not found.";
    }

    // Parking statistics
    public void getStatistics() {

        int occupied = totalParked;
        double avgProbes = totalParked == 0 ? 0.0 : ((double) totalProbes / totalParked);
        double occupancyPercent = (occupied * 100.0) / capacity;

        System.out.println("=== Parking Lot Statistics ===");
        System.out.println("Current Occupancy: " + String.format("%.2f", occupancyPercent) + "%");
        System.out.println("Average Probes per Park: " + String.format("%.2f", avgProbes));
        System.out.println("Peak Occupancy: " + maxOccupancy + " vehicles");
        System.out.println("==============================");
    }
}

public class Week1_and_Week2_Problems {

    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot(500);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        Thread.sleep(2000); // simulate 2 seconds parked

        System.out.println(lot.exitVehicle("ABC-1234"));
        System.out.println(lot.exitVehicle("XYZ-9999"));

        lot.getStatistics();
    }
}