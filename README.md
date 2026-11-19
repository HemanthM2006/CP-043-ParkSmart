# CP-043 ParkSmart: Multi-Level Parking Allocation and Billing System

## Team
1. Hemanth M (1AT24CS074)
2. Mohammed Ali Farooq (1AT24CS117)

## How to run
javac Main.java
java Main

## Features that work
1. Configure multi-level parking lots with separate slot counts for two-wheelers (`2W`), cars (`CAR`), and electric vehicles (`EV`)
2. Assign the nearest available slot matching the arriving vehicle's type on entry and issue a formatted ticket
3. Calculate exit parking fees based on parked duration using progressive hourly slabs (Hours 1–2, Hours 3–5, and >5 Hours) and vacate the slot
4. Display live occupancy counts and slot-by-slot status across every level and vehicle type
5. Report the day's total revenue, vehicle entry/exit counts, and completed bill history
6. Stretch Feature: Charge EV slots an additional fee based on charging duration (Rs. 0.50/min)
7. Stretch Feature: Handle lost tickets on exit by looking up the vehicle registration number and applying a fixed Rs. 200 penalty
8. Stretch Feature: Automatically save completed exit billing records to `parking_bills.csv`

## Sample run
```text
=======================================================
   PARKSMART: MULTI-LEVEL PARKING & BILLING SYSTEM
=======================================================
1. Set Up Parking Levels and Slots (Core Feature 1)
2. Vehicle Entry & Slot Allocation (Core Feature 2)
3. Vehicle Exit & Slab Billing     (Core Feature 3)
4. View Live Occupancy Status      (Core Feature 4)
5. View Day's Revenue & Summary    (Core Feature 5)
6. Exit Application
-------------------------------------------------------
Enter your choice (1-6): 2

--- VEHICLE ENTRY & SLOT ALLOCATION ---
Enter Vehicle Registration Number (e.g., KA01AB1234): KA01AB1234
Select Vehicle Type: 1. Two-Wheeler (2W)   2. Car (CAR)   3. Electric Vehicle (EV)
Enter type (1-3): 3
Enter Entry Time in 24-hr HH:MM format (e.g., 09:30): 09:10

+-----------------------------------------+
|          PARKSMART ENTRY TICKET         |
+-----------------------------------------+
| Ticket ID      : 1001                   |
| Vehicle Number : KA01AB1234             |
| Vehicle Type   : EV                     |
| Assigned Slot  : Level 1 - Slot 8       |
| Entry Time     : 09:10                  |
+-----------------------------------------+

Enter your choice (1-6): 3

--- VEHICLE EXIT & BILLING ---
1. Standard Exit (With Ticket ID)
2. Lost Ticket Exit (Search by Vehicle Number + Rs. 200 Penalty)
Select Exit Mode (1 or 2): 1
Enter Ticket ID: 1001
Enter Exit Time in 24-hr HH:MM format (Entry was 09:10): 10:00
Enter EV charging duration in minutes (0 to 50): 50

+---------------------------------------------+
|            PARKSMART EXIT RECEIPT           |
+---------------------------------------------+
| Ticket ID        : 1001                     |
| Vehicle Number   : KA01AB1234               |
| Vehicle Type     : EV                       |
| Vacated Slot     : Level 1 - Slot 8         |
| Entry -> Exit    : 09:10 -> 10:00           |
| Duration         : 50 mins (1 billed hr(s)) |
+---------------------------------------------+
| Slab Parking Fee : Rs. 30.00                |
| EV Charging Fee  : Rs. 25.00                |
+---------------------------------------------+
| GRAND TOTAL      : Rs. 55.00                |
+---------------------------------------------+