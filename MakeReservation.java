import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MakeReservation extends Reservation {
    private Scanner scanner = new Scanner(System.in);
    private DBConnector dbConnector = new DBConnector();

    private boolean isSaved = false;  // Flag to track if reservation has been saved or not

    // Implementing abstract method to gather reservation details
    @Override
    public void gatherReservationDetails() {
        clear(); // Simulate the clear screen
        System.out.println("Reservation Details");

        // Get user information
        System.out.print("\nEnter First Name (or Press 'Enter' to return on main menu): ");
        firstName = scanner.nextLine();
        if (firstName.isEmpty()) {
            return;
        }

        System.out.print("\nEnter Last Name: ");
        lastName = scanner.nextLine();

        // Contact number validation
        contact = getValidContactNumber();

        // Automatically set the reservation date and time to the next available slot
        reservationData = getNextAvailableReservationDate();

        // Get number of guests and special requests
        System.out.print("\nEnter Number of Guests: ");
        numGuests = Integer.parseInt(scanner.nextLine());

        System.out.print("\nEnter Special Request (or press Enter for none): ");
        String specialReq = scanner.nextLine();
        specialRequest = specialReq.isEmpty() ? null : specialReq;
    }

    // Method to validate the contact number
    private String getValidContactNumber() {
        String contactNumber;
        Pattern pattern = Pattern.compile("^\\d{10}$"); // Only digits and exactly 10 digits
        Matcher matcher;

        while (true) {
            System.out.print("\nEnter Contact Number (10 digits): ");
            contactNumber = scanner.nextLine();
            matcher = pattern.matcher(contactNumber);

            if (matcher.matches()) {
                break; // Valid contact number, exit loop
            } else {
                System.out.println("\nInvalid contact number. Please enter exactly 10 digits.");
            }
        }

        return contactNumber;
    }

    // Method to get the next available reservation date and time
    private String getNextAvailableReservationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1); // Default to the next hour

        while (true) {
            String potentialDateTime = sdf.format(calendar.getTime());

            if (!isReservationSlotTaken(potentialDateTime)) {
                System.out.println("\nAutomatically setting your reservation date and time to: " + potentialDateTime);
                return potentialDateTime;
            }

            calendar.add(Calendar.HOUR, 1); // Check the next hour
        }
    }

    // Method to check if a reservation slot is already taken
    private boolean isReservationSlotTaken(String dateTime) {
        String query = "SELECT * FROM reservations WHERE reservation_date = '" + dateTime + "'";

        try {
            dbConnector.db_connector();
            dbConnector.res = dbConnector.stmt.executeQuery(query);

            if (dbConnector.res.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Check for duplicate reservation in the database
    private boolean isDuplicateReservation() {
        String query = "SELECT * FROM reservations WHERE contact = '" + contact + "' AND reservation_date = '" + reservationData + "'";

        try {
            dbConnector.db_connector();
            dbConnector.res = dbConnector.stmt.executeQuery(query);

            if (dbConnector.res.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // No duplicate found
    }

    // Prompt user for editing if duplicate reservation exists
    private boolean handleDuplicateReservation() {
        System.out.println("\nDuplicate reservation found for this contact number and date.");
        System.out.print("Do you want to edit the existing reservation? (Y/N): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y")) {
            editExistingReservation();
            return true; // Proceed with editing
        } else {
            System.out.println("Reservation not saved. Returning to main menu.");
            return false; // Do not proceed
        }
    }

    // Edit the existing reservation (update in DB)
    private void editExistingReservation() {
        String query = "UPDATE reservations SET name = '" + mergeNames() + "', num_guests = " + numGuests + ", special_request = '" + (specialRequest == null ? "NULL" : specialRequest) + "' WHERE contact = '" + contact + "' AND reservation_date = '" + reservationData + "'";

        try {
            dbConnector.db_connector();
            dbConnector.stmt.executeUpdate(query);
            dbConnector.con.close();
            System.out.println("Existing reservation updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Implementing abstract method to confirm reservation details
    @Override
    public boolean confirmReservation() {
        // Check if there is already a reservation with the same contact and date and time
        if (isDuplicateReservation()) {
            return handleDuplicateReservation();
        }

        // If no duplicate, proceed with reservation saving
        clear();
        System.out.println("\nReservation Summary:");
        System.out.println("Name: " + mergeNames());
        System.out.println("Contact: " + contact);
        System.out.println("Reservation Date and Time: " + reservationData);
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Special Request: " + (specialRequest == null ? "None" : specialRequest));

        System.out.print("\nDo you want to proceed with the reservation? (Y/N): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y")) {
            // Save to the database
            saveReservation();
            System.out.println("Reservation successfully made.");
            isSaved = true;
        } else {
            System.out.println("Reservation was not saved.");
            isSaved = false;
        }

        return isSaved;
    }

    // Implementing abstract method to save reservation to the database
    @Override
    public void saveReservation() {
        String query = "INSERT INTO reservations (name, contact, reservation_date, num_guests, special_request) VALUES ('"
                        + mergeNames() + "', '"
                        + contact + "', '"
                        + reservationData + "', "
                        + numGuests + ", '"
                        + (specialRequest == null ? "NULL" : specialRequest) + "')";

        try {
            dbConnector.db_connector();
            dbConnector.stmt.executeUpdate(query);
            dbConnector.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Implementing abstract method to merge first and last names
    @Override
    public String mergeNames() {
        // Capitalize first and last name
        String fullName = (firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase())
                        + " "
                        + (lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase());
        return fullName;
    }

    // Method to handle reservation flow (gather details, confirm, save)
    public boolean makeReservation() {
        gatherReservationDetails();
        if (firstName.isEmpty()) {
            return false;
        }
        return confirmReservation(); // Return whether the reservation was saved
    }

    // Simulate the clear screen
    public void clear() {
        for (int i = 0; i < 11; i++) {
            System.out.println("\n");
        }
    }
}




