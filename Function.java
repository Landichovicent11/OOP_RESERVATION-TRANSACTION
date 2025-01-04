import java.util.Scanner;

public class Function extends Display {
    private Scanner Input = new Scanner(System.in);
    private String input;
    private char LowerInput;
    private int arrow = 0;
    
    //Check for the user input 
    public void KeyinputMenu() {
        DisplayMenu(arrow);
        input = Input.nextLine();

        if (input.length() == 1) {//check if user input is 1
        	
            char inputChar = input.charAt(0);//convert it to character
            
            LowerInput = Character.toLowerCase(inputChar);//lowercased the inputed key
            
            ReloaderMenu();//load the main menu
        } else {
            System.out.println("Error: Please enter only one character.");
        }
    }
//check if user press Enter
    public void KeyInputView() {
    	
    	DisplayView();
        input = Input.nextLine();

        if (input.isEmpty()) {
            // If Enter is pressed, go back to the main menu
            KeyinputMenu();
        }
    }
    
    public void ReloaderMenu() {
        switch (LowerInput) {
            case 'w':
                // Check if arrow is not out of bounds
                if (arrow == 0) {
                    KeyinputMenu();
                } else {
                    arrow--;
                    KeyinputMenu();
                }
                break;
            case 's':
                // Check if arrow is not out of bounds
                if (arrow == 2) {
                    KeyinputMenu();
                } else {
                    arrow++;
                    KeyinputMenu();
                }
                break;
            case 'y':
                // Make reservation, view reservations, or exit
                if (arrow == 0) {
                	 // Create a new reservation
                    MakeReservation reservation = new MakeReservation();
                    reservation.gatherReservationDetails(); // Gather reservation details
                    boolean isSaved = reservation.confirmReservation(); // Confirm and check if the reservation is saved
                    if (!isSaved) {
                        KeyinputMenu(); // Go back to the main menu
                    } else {
                        KeyinputMenu(); // Go back to the main menu
                    }
                } else if (arrow == 1) {
                    KeyInputView(); // View all reservations
                } else if (arrow == 2) {
                    System.exit(0); // Exit
                }
                break;
        }
    }
 }




