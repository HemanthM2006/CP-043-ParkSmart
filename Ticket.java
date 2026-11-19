public class Ticket {
    int ticketId;
    String vehicleNumber;
    String vehicleType; // "2W", "CAR", or "EV"
    int levelNumber;
    int slotNumber;
    String entryTimeStr;
    int entryMinutes;   // Minutes from 00:00 for easy duration math
    String exitTimeStr;
    int exitMinutes;
    boolean isActive;
    double baseParkingFee;
    double evChargingFee;
    double lostTicketPenalty;
    double totalFee;

    public Ticket(int ticketId, String vehicleNumber, String vehicleType,
                  int levelNumber, int slotNumber, String entryTimeStr, int entryMinutes) {
        this.ticketId = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.levelNumber = levelNumber;
        this.slotNumber = slotNumber;
        this.entryTimeStr = entryTimeStr;
        this.entryMinutes = entryMinutes;
        this.exitTimeStr = "-";
        this.exitMinutes = 0;
        this.isActive = true;
        this.baseParkingFee = 0.0;
        this.evChargingFee = 0.0;
        this.lostTicketPenalty = 0.0;
        this.totalFee = 0.0;
    }
}
