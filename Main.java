import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    // 2D array: lot[levelIndex][slotIndex]
    static ParkingSlot[][] lot;
    static int totalLevels = 0;

    // Fixed-size array to store up to 500 tickets for the day
    static Ticket[] tickets = new Ticket[500];
    static int ticketCount = 0;
    static int nextTicketId = 1001;

    // Daily summary metrics
    static int totalVehiclesEntered = 0;
    static int totalVehiclesExited = 0;
    static double totalRevenue = 0.0;

    // Penalty constant for lost ticket stretch feature
    static final double LOST_TICKET_PENALTY = 200.0;

    public static void main(String[] args) {
        // Initialize a default 2-level lot so the app is ready to demo immediately
        initializeDefaultLot();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice (1-6): ");

            switch (choice) {
                case 1:
                    setupParkingLot();
                    break;
                case 2:
                    parkVehicle();
                    break;
                case 3:
                    exitAndBillVehicle();
                    break;
                case 4:
                    showLiveOccupancy();
                    break;
                case 5:
                    showDailySummary();
                    break;
                case 6:
                    System.out.println("\nExiting ParkSmart System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select an option between 1 and 6.");
            }
        }
    }

    static void printMenu() {
        System.out.println("\n=======================================================");
        System.out.println("   PARKSMART: MULTI-LEVEL PARKING & BILLING SYSTEM");
        System.out.println("=======================================================");
        System.out.println("1. Set Up Parking Levels and Slots (Core Feature 1)");
        System.out.println("2. Vehicle Entry & Slot Allocation (Core Feature 2)");
        System.out.println("3. Vehicle Exit & Slab Billing     (Core Feature 3)");
        System.out.println("4. View Live Occupancy Status      (Core Feature 4)");
        System.out.println("5. View Day's Revenue & Summary    (Core Feature 5)");
        System.out.println("6. Exit Application");
        System.out.println("-------------------------------------------------------");
    }

    // Pre-loads a 2-level lot (each level: 3 Two-Wheelers, 4 Cars, 2 EVs)
    static void initializeDefaultLot() {
        totalLevels = 2;
        lot = new ParkingSlot[totalLevels][];

        for (int lvl = 0; lvl < totalLevels; lvl++) {
            int twoW = 3, cars = 4, evs = 2;
            lot[lvl] = new ParkingSlot[twoW + cars + evs];
            int slotNum = 1;

            for (int i = 0; i < twoW; i++) {
                lot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "2W");
            }
            for (int i = 0; i < cars; i++) {
                lot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "CAR");
            }
            for (int i = 0; i < evs; i++) {
                lot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "EV");
            }
        }
    }

    // ---------------- CORE FEATURE 1: SET UP LEVELS & SLOTS ----------------
    static void setupParkingLot() {
        if (getActiveParkedCount() > 0) {
            System.out.println("Error: Cannot reconfigure lot while vehicles are parked inside!");
            return;
        }

        System.out.println("\n--- SET UP PARKING LOT LEVELS & SLOTS ---");
        int levels = readInt("Enter number of parking levels (1-10): ");
        if (levels <= 0 || levels > 10) {
            System.out.println("Error: Number of levels must be between 1 and 10.");
            return;
        }

        ParkingSlot[][] newLot = new ParkingSlot[levels][];

        for (int lvl = 0; lvl < levels; lvl++) {
            System.out.println("\nConfigure Level " + (lvl + 1) + ":");
            int twoW = readInt("  Number of Two-Wheeler (2W) slots: ");
            int cars = readInt("  Number of Car (CAR) slots: ");
            int evs  = readInt("  Number of Electric Vehicle (EV) slots: ");

            if (twoW < 0 || cars < 0 || evs < 0 || (twoW + cars + evs == 0)) {
                System.out.println("Error: Slot counts cannot be negative, and total slots on a level must be at least 1.");
                return;
            }

            int totalSlotsOnLevel = twoW + cars + evs;
            newLot[lvl] = new ParkingSlot[totalSlotsOnLevel];
            int slotNum = 1;

            for (int i = 0; i < twoW; i++) {
                newLot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "2W");
            }
            for (int i = 0; i < cars; i++) {
                newLot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "CAR");
            }
            for (int i = 0; i < evs; i++) {
                newLot[lvl][slotNum - 1] = new ParkingSlot(lvl + 1, slotNum++, "EV");
            }
        }

        lot = newLot;
        totalLevels = levels;
        System.out.println("Success: Parking lot configured with " + totalLevels + " level(s).");
    }

    // ---------------- CORE FEATURE 2: VEHICLE ENTRY & ALLOCATION ----------------
    static void parkVehicle() {
        System.out.println("\n--- VEHICLE ENTRY & SLOT ALLOCATION ---");
        System.out.print("Enter Vehicle Registration Number (e.g., KA01AB1234): ");
        String vehicleNo = sc.nextLine().trim().toUpperCase();

        if (vehicleNo.isEmpty()) {
            System.out.println("Error: Vehicle number cannot be empty.");
            return;
        }

        if (isVehicleAlreadyParked(vehicleNo)) {
            System.out.println("Error: Vehicle " + vehicleNo + " is already parked inside the lot!");
            return;
        }

        System.out.println("Select Vehicle Type: 1. Two-Wheeler (2W)   2. Car (CAR)   3. Electric Vehicle (EV)");
        int typeChoice = readInt("Enter type (1-3): ");
        String vType = "";
        if (typeChoice == 1) vType = "2W";
        else if (typeChoice == 2) vType = "CAR";
        else if (typeChoice == 3) vType = "EV";
        else {
            System.out.println("Error: Invalid vehicle type selected.");
            return;
        }

        System.out.print("Enter Entry Time in 24-hr HH:MM format (e.g., 09:30): ");
        String entryTimeStr = sc.nextLine().trim();
        int entryMins = parseTimeToMinutes(entryTimeStr);
        if (entryMins == -1) {
            System.out.println("Error: Invalid time format! Please use HH:MM (00:00 to 23:59).");
            return;
        }

        // Find nearest free slot of matching type (searches Level 1 upwards, Slot 1 upwards)
        ParkingSlot assignedSlot = null;
        for (int lvl = 0; lvl < totalLevels; lvl++) {
            for (int s = 0; s < lot[lvl].length; s++) {
                ParkingSlot candidate = lot[lvl][s];
                if (!candidate.isOccupied && candidate.slotType.equals(vType)) {
                    assignedSlot = candidate;
                    break;
                }
            }
            if (assignedSlot != null) break;
        }

        if (assignedSlot == null) {
            System.out.println("Sorry! All " + vType + " slots are currently FULL across all levels.");
            return;
        }

        int ticketId = nextTicketId++;
        assignedSlot.park(vehicleNo, ticketId);

        Ticket newTicket = new Ticket(
                ticketId, vehicleNo, vType,
                assignedSlot.levelNumber, assignedSlot.slotNumber,
                entryTimeStr, entryMins
        );
        tickets[ticketCount++] = newTicket;
        totalVehiclesEntered++;

        System.out.println("\n+-----------------------------------------+");
        System.out.println("|          PARKSMART ENTRY TICKET         |");
        System.out.println("+-----------------------------------------+");
        System.out.printf("| Ticket ID      : %-22d |\n", newTicket.ticketId);
        System.out.printf("| Vehicle Number : %-22s |\n", newTicket.vehicleNumber);
        System.out.printf("| Vehicle Type   : %-22s |\n", newTicket.vehicleType);
        System.out.printf("| Assigned Slot  : Level %d - Slot %-7d |\n", newTicket.levelNumber, newTicket.slotNumber);
        System.out.printf("| Entry Time     : %-22s |\n", newTicket.entryTimeStr);
        System.out.println("+-----------------------------------------+");
    }

    // ---------------- CORE FEATURE 3: EXIT & SLAB BILLING ----------------
    static void exitAndBillVehicle() {
        System.out.println("\n--- VEHICLE EXIT & BILLING ---");
        System.out.println("1. Standard Exit (With Ticket ID)");
        System.out.println("2. Lost Ticket Exit (Search by Vehicle Number + Rs. 200 Penalty)");
        int exitMode = readInt("Select Exit Mode (1 or 2): ");

        Ticket activeTicket = null;
        boolean isLostTicket = false;

        if (exitMode == 1) {
            int tId = readInt("Enter Ticket ID: ");
            activeTicket = findActiveTicketById(tId);
            if (activeTicket == null) {
                System.out.println("Error: No active parked vehicle found with Ticket ID " + tId + ".");
                return;
            }
        } else if (exitMode == 2) {
            System.out.print("Enter Vehicle Registration Number: ");
            String vNo = sc.nextLine().trim().toUpperCase();
            activeTicket = findActiveTicketByVehicle(vNo);
            if (activeTicket == null) {
                System.out.println("Error: No active parked vehicle found with number " + vNo + ".");
                return;
            }
            isLostTicket = true;
        } else {
            System.out.println("Error: Invalid exit mode choice.");
            return;
        }

        System.out.print("Enter Exit Time in 24-hr HH:MM format (Entry was " + activeTicket.entryTimeStr + "): ");
        String exitTimeStr = sc.nextLine().trim();
        int exitMins = parseTimeToMinutes(exitTimeStr);

        if (exitMins == -1) {
            System.out.println("Error: Invalid time format! Please use HH:MM (00:00 to 23:59).");
            return;
        }
        if (exitMins < activeTicket.entryMinutes) {
            System.out.println("Error: Exit time cannot be earlier than entry time (" + activeTicket.entryTimeStr + ")!");
            return;
        }

        int durationMins = exitMins - activeTicket.entryMinutes;
        // Round up to nearest hour; minimum 1 hour billed
        int billedHours = (durationMins == 0) ? 1 : (durationMins + 59) / 60;

        double baseFee = calculateSlabFee(activeTicket.vehicleType, billedHours);
        double evChargeFee = 0.0;

        // Stretch Feature 1: Charge EV slots extra for charging time
        if (activeTicket.vehicleType.equals("EV")) {
            int chargeMins = readInt("Enter EV charging duration in minutes (0 to " + durationMins + "): ");
            if (chargeMins < 0 || chargeMins > durationMins) {
                System.out.println("Error: Charging time must be between 0 and total parked minutes (" + durationMins + ").");
                return;
            }
            evChargeFee = chargeMins * 0.50; // Rs. 0.50 per minute (Rs. 30/hr)
        }

        // Stretch Feature 2: Handle lost ticket with a fixed penalty
        double penalty = isLostTicket ? LOST_TICKET_PENALTY : 0.0;
        double finalTotal = baseFee + evChargeFee + penalty;

        // Update Ticket and Slot state
        activeTicket.exitTimeStr = exitTimeStr;
        activeTicket.exitMinutes = exitMins;
        activeTicket.baseParkingFee = baseFee;
        activeTicket.evChargingFee = evChargeFee;
        activeTicket.lostTicketPenalty = penalty;
        activeTicket.totalFee = finalTotal;
        activeTicket.isActive = false;

        ParkingSlot slot = lot[activeTicket.levelNumber - 1][activeTicket.slotNumber - 1];
        slot.vacate();

        totalVehiclesExited++;
        totalRevenue += finalTotal;

        // Print itemized receipt
        System.out.println("\n+---------------------------------------------+");
        System.out.println("|            PARKSMART EXIT RECEIPT           |");
        System.out.println("+---------------------------------------------+");
        System.out.printf("| Ticket ID        : %-24d |\n", activeTicket.ticketId);
        System.out.printf("| Vehicle Number   : %-24s |\n", activeTicket.vehicleNumber);
        System.out.printf("| Vehicle Type     : %-24s |\n", activeTicket.vehicleType);
        System.out.printf("| Vacated Slot     : Level %d - Slot %-9d |\n", activeTicket.levelNumber, activeTicket.slotNumber);
        System.out.printf("| Entry -> Exit    : %s -> %-15s |\n", activeTicket.entryTimeStr, activeTicket.exitTimeStr);
        System.out.printf("| Duration         : %d mins (%d billed hr(s)) |\n", durationMins, billedHours);
        System.out.println("+---------------------------------------------+");
        System.out.printf("| Slab Parking Fee : Rs. %-20.2f |\n", baseFee);
        if (activeTicket.vehicleType.equals("EV")) {
            System.out.printf("| EV Charging Fee  : Rs. %-20.2f |\n", evChargeFee);
        }
        if (isLostTicket) {
            System.out.printf("| Lost Ticket Fine : Rs. %-20.2f |\n", penalty);
        }
        System.out.println("+---------------------------------------------+");
        System.out.printf("| GRAND TOTAL      : Rs. %-20.2f |\n", finalTotal);
        System.out.println("+---------------------------------------------+");

        saveBillToCsv(activeTicket, durationMins, billedHours);
    }

    /*
     * Hourly Slab Pricing Logic:
     * - Slab 1 (Hours 1 to 2): Base Rate (2W: Rs 20/hr, CAR: Rs 40/hr, EV: Rs 30/hr)
     * - Slab 2 (Hours 3 to 5): 1.5x Rate (2W: Rs 30/hr, CAR: Rs 60/hr, EV: Rs 45/hr)
     * - Slab 3 (Above 5 hrs) : 2.0x Rate (2W: Rs 40/hr, CAR: Rs 80/hr, EV: Rs 60/hr)
     */
    static double calculateSlabFee(String type, int hours) {
        double baseRate = 0.0;
        if (type.equals("2W")) baseRate = 20.0;
        else if (type.equals("CAR")) baseRate = 40.0;
        else if (type.equals("EV")) baseRate = 30.0;

        double fee = 0.0;
        for (int h = 1; h <= hours; h++) {
            if (h <= 2) {
                fee += baseRate;
            } else if (h <= 5) {
                fee += baseRate * 1.5;
            } else {
                fee += baseRate * 2.0;
            }
        }
        return fee;
    }

    // ---------------- CORE FEATURE 4: LIVE OCCUPANCY ----------------
    static void showLiveOccupancy() {
        System.out.println("\n--- LIVE PARKING OCCUPANCY PER LEVEL & TYPE ---");
        System.out.printf("%-8s | %-18s | %-18s | %-18s\n",
                "LEVEL", "2W (Occ/Total)", "CAR (Occ/Total)", "EV (Occ/Total)");
        System.out.println("-----------------------------------------------------------------------");

        for (int lvl = 0; lvl < totalLevels; lvl++) {
            int twoWTot = 0, twoWOcc = 0;
            int carTot = 0, carOcc = 0;
            int evTot = 0, evOcc = 0;

            for (int s = 0; s < lot[lvl].length; s++) {
                ParkingSlot slot = lot[lvl][s];
                if (slot.slotType.equals("2W")) {
                    twoWTot++;
                    if (slot.isOccupied) twoWOcc++;
                } else if (slot.slotType.equals("CAR")) {
                    carTot++;
                    if (slot.isOccupied) carOcc++;
                } else if (slot.slotType.equals("EV")) {
                    evTot++;
                    if (slot.isOccupied) evOcc++;
                }
            }

            System.out.printf("Level %-2d | %d/%d (Free: %-2d)     | %d/%d (Free: %-2d)     | %d/%d (Free: %-2d)\n",
                    (lvl + 1),
                    twoWOcc, twoWTot, (twoWTot - twoWOcc),
                    carOcc, carTot, (carTot - carOcc),
                    evOcc, evTot, (evTot - evOcc));
        }

        System.out.println("\nDetailed Slot Status:");
        for (int lvl = 0; lvl < totalLevels; lvl++) {
            System.out.print("Level " + (lvl + 1) + ": ");
            for (int s = 0; s < lot[lvl].length; s++) {
                ParkingSlot slot = lot[lvl][s];
                String state = slot.isOccupied ? slot.vehicleNumber : "FREE";
                System.out.print("[S" + slot.slotNumber + "-" + slot.slotType + ":" + state + "] ");
            }
            System.out.println();
        }
    }

    // ---------------- CORE FEATURE 5: DAILY REVENUE & SUMMARY ----------------
    static void showDailySummary() {
        System.out.println("\n--- DAY'S REVENUE AND VEHICLE SUMMARY ---");
        System.out.println("Total Vehicles Entered Today : " + totalVehiclesEntered);
        System.out.println("Vehicles Currently Parked    : " + getActiveParkedCount());
        System.out.println("Total Vehicles Exited (Billed): " + totalVehiclesExited);
        System.out.printf("Total Revenue Collected      : Rs. %.2f\n", totalRevenue);

        if (totalVehiclesExited > 0) {
            System.out.println("\nCompleted Bills Today:");
            System.out.printf("%-10s | %-12s | %-5s | %-13s | %-10s\n",
                    "Ticket ID", "Vehicle No", "Type", "Entry->Exit", "Total Fee");
            System.out.println("--------------------------------------------------------------");
            for (int i = 0; i < ticketCount; i++) {
                Ticket t = tickets[i];
                if (!t.isActive) {
                    System.out.printf("%-10d | %-12s | %-5s | %s->%-6s | Rs. %.2f\n",
                            t.ticketId, t.vehicleNumber, t.vehicleType,
                            t.entryTimeStr, t.exitTimeStr, t.totalFee);
                }
            }
        }
    }

    // ---------------- HELPER & VALIDATION METHODS ----------------
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid whole number.");
            }
        }
    }

    static int parseTimeToMinutes(String timeStr) {
        String[] parts = timeStr.split(":");
        if (parts.length != 2) return -1;
        try {
            int hrs = Integer.parseInt(parts[0]);
            int mins = Integer.parseInt(parts[1]);
            if (hrs < 0 || hrs > 23 || mins < 0 || mins > 59) return -1;
            return hrs * 60 + mins;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static boolean isVehicleAlreadyParked(String vehicleNo) {
        for (int i = 0; i < ticketCount; i++) {
            if (tickets[i].isActive && tickets[i].vehicleNumber.equalsIgnoreCase(vehicleNo)) {
                return true;
            }
        }
        return false;
    }

    static Ticket findActiveTicketById(int ticketId) {
        for (int i = 0; i < ticketCount; i++) {
            if (tickets[i].isActive && tickets[i].ticketId == ticketId) {
                return tickets[i];
            }
        }
        return null;
    }

    static Ticket findActiveTicketByVehicle(String vehicleNo) {
        for (int i = 0; i < ticketCount; i++) {
            if (tickets[i].isActive && tickets[i].vehicleNumber.equalsIgnoreCase(vehicleNo)) {
                return tickets[i];
            }
        }
        return null;
    }

    static int getActiveParkedCount() {
        int count = 0;
        for (int i = 0; i < ticketCount; i++) {
            if (tickets[i].isActive) count++;
        }
        return count;
    }

    // Stretch Feature: Save billing record to CSV file
    static void saveBillToCsv(Ticket t, int durationMins, int billedHours) {
        try (FileWriter fw = new FileWriter("parking_bills.csv", true)) {
            fw.write(t.ticketId + "," + t.vehicleNumber + "," + t.vehicleType + ",Level-" +
                    t.levelNumber + "-Slot-" + t.slotNumber + "," + t.entryTimeStr + "," +
                    t.exitTimeStr + "," + durationMins + "m," + billedHours + "h," +
                    t.totalFee + "\n");
        } catch (IOException e) {
            System.out.println("Note: Could not write bill to CSV file.");
        }
    }
}
