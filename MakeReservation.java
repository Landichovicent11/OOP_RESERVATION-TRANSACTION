import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MakeReservation extends Reservation {
    private Scanner scanner = new Scanner(System.in);
    private DBConnector dbConnector = new DBConnector();

    private boolean isSaved = false; // Flag to track if reservation has been saved or not

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
                System.out.println("\nSet Reservation date and time to: " + potentialDateTime);
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
    // Check for duplicate reservation by name
    private boolean checkDuplicateByName() {
        String query = "SELECT * FROM reservations WHERE name = '" + mergeNames() + "'";

        try {
            dbConnector.db_connector();
            dbConnector.res = dbConnector.stmt.executeQuery(query);

            if (dbConnector.res.next()) {
            	clear();
                System.out.println("\nA reservation with this name already exists:\n");
                System.out.println("Reservation Date and Time: " + dbConnector.res.getString("reservation_date"));
                System.out.println("Number of Guests: " + dbConnector.res.getInt("num_guests"));
                System.out.println("Special Request: " + dbConnector.res.getString("special_request"));

                System.out.print("\nDo you want to edit the existing reservation? (Y/N): ");
                String choice = scanner.nextLine().toLowerCase();

                if (choice.equals("y")) {
                    editExistingReservation();
                    return true; // Proceed with editing
                } else {
                    System.out.println("\nDo you want to make a new reservation instead? (Y/N): ");
                    String newChoice = scanner.nextLine().toLowerCase();

                    if (newChoice.equals("y")) {
                        gatherReservationDetails(); // Gather new details for another reservation
                        return false; // Continue to save new reservation
                    } else {
                        System.out.println("Returning to main menu.");
                        return true; // Exit without saving
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // No duplicate found
    }

    private void editExistingReservation() {
        try {
            // Fetch the existing reservation details
            String query = "SELECT * FROM reservations WHERE name = '" + mergeNames() + "'";
            dbConnector.db_connector();
            dbConnector.res = dbConnector.stmt.executeQuery(query);

            if (dbConnector.res.next()) {
                // Display current details
            	clear();
                System.out.println("\nCurrent Reservation Details:");
                String currentContact = dbConnector.res.getString("contact");
                String currentDate = dbConnector.res.getString("reservation_date");
                int currentGuests = dbConnector.res.getInt("num_guests");
                String currentRequest = dbConnector.res.getString("special_request");

                System.out.println("Contact: " + currentContact);
                System.out.println("Reservation Date and Time: " + currentDate);
                System.out.println("Number of Guests: " + currentGuests);
                System.out.println("Special Request: " + (currentRequest == null ? "None" : currentRequest));

                // Prompt user for new details
                System.out.print("\nEnter new Contact (or press Enter to keep current): ");
                String newContact = scanner.nextLine();
                if (newContact.isEmpty()) {
                    newContact = currentContact;
                }

                System.out.print("Do you want to Get a new reservation Date Input(Y/Enter if no):");
                String newDate = scanner.nextLine();
                if (newDate.isEmpty()) {
                    newDate = currentDate;
                }
                else {
                	newDate = getNextAvailableReservationDate();
                }

                System.out.print("Enter new Number of Guests (or press Enter to keep current): ");
                String newGuestsInput = scanner.nextLine();
                int newGuests = newGuestsInput.isEmpty() ? currentGuests : Integer.parseInt(newGuestsInput);

                System.out.print("Enter new Special Request (or press Enter to keep current): ");
                String newRequest = scanner.nextLine();
                if (newRequest.isEmpty()) {
                    newRequest = currentRequest;
                }

                // Update the reservation in the database
                String updateQuery = "UPDATE reservations SET contact = '" + newContact +
                                     "', reservation_date = '" + newDate +
                                     "', num_guests = " + newGuests +
                                     ", special_request = '" + (newRequest == null ? "NULL" : newRequest) +
                                     "' WHERE name = '" + mergeNames() + "'";
                dbConnector.stmt.executeUpdate(updateQuery);
                dbConnector.con.close();

                System.out.println("\nReservation updated successfully.");
            } else {
                System.out.println("\nNo reservation found for the provided name.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Implementing abstract method to confirm reservation details
    @Override
    public boolean confirmReservation() {
        if (checkDuplicateByName()) {
            return isSaved; // Handle duplicate and return the saved status
        }

        // If no duplicate, proceed with reservation saving
        clear();
        System.out.println("\nReservation Summary:");
        System.out.println("Name: " + mergeNames());
        System.out.println("Contact: " + contact);
        System.out.println("Reservation Date and Time: " + reservationData);
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Special Request: " + (specialRequest == null ? "None" : specialRequest));

        //Prompt the user if he want to Proceed in make a reservation
        System.out.print("\nDo you want to proceed with the reservation? (Y/N): ");
        String choice = scanner.nextLine().toLowerCase();
        
        if (choice.equals("y")) {
            saveReservation();
            System.out.println("Reservation successfully made.");
            isSaved = true;
        } else {
            System.out.println("Reservation was not saved.");
            isSaved = false;
        }

        return isSaved;
    }

    @Override
    //Save the reservation details
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

    @Override
    //combine the firstname to lastname
    public String mergeNames() {
        return (firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase())
                + " " + (lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase());
    }
//simulate clear line
    public void clear() {
        for (int i = 0; i < 11; i++) {
            System.out.println("\n");
        }
    }
}





