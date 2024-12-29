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
        input = Input.nextLine();

        if (input.isEmpty()) {
            // If Enter is pressed, go back to the main menu
            KeyinputMenu();
        }
    }
    
    //print the main menu every time the user press key
    public void ReloaderMenu() {
        switch (LowerInput) {
            case 'w':
            	//check it arrow is not out of bound
                if (arrow == 0) {
                    KeyinputMenu();
                } else {
                    arrow--;
                    KeyinputMenu();
                }
                break;
            case 's':
            	//check if arrow is not out of bound
                if (arrow == 2) {
                    KeyinputMenu();
                } else {
                    arrow++;
                    KeyinputMenu();
                }
                break;
            case 'y':
            	//check if user choose Make reservation
                if (arrow == 0) {
                    // Make reservation using the abstract Reservation class
                    MakeReservation reservation = new MakeReservation();
                    boolean isSaved = reservation.makeReservation();
                    if (!isSaved) {
                        KeyinputMenu();//go back Main Menu
                    }else {
                    	KeyinputMenu();//go back Main menu
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




