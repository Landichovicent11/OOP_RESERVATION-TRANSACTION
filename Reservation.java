
public abstract class Reservation {
    protected String firstName;
    protected String lastName;
    protected String contact;
    protected String reservationData;
    protected int numGuests;
    protected String specialRequest;

    // Abstract methods that need to be implemented by concrete classes
    public abstract void gatherReservationDetails();
    public abstract boolean confirmReservation();
    public abstract void saveReservation();
    public abstract String mergeNames();  // Merge first and last names with proper capitalization
}

