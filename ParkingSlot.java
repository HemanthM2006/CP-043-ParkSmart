public class ParkingSlot {
    int levelNumber;
    int slotNumber;
    String slotType; // "2W", "CAR", or "EV"
    boolean isOccupied;
    String vehicleNumber;
    int activeTicketId;

    public ParkingSlot(int levelNumber, int slotNumber, String slotType) {
        this.levelNumber = levelNumber;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.isOccupied = false;
        this.vehicleNumber = "";
        this.activeTicketId = -1;
    }

    public void park(String vehicleNumber, int ticketId) {
        this.isOccupied = true;
        this.vehicleNumber = vehicleNumber;
        this.activeTicketId = ticketId;
    }

    public void vacate() {
        this.isOccupied = false;
        this.vehicleNumber = "";
        this.activeTicketId = -1;
    }
}
