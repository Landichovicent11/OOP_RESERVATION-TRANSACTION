
public abstract class Display {
    DBConnector db = new DBConnector();
    
    //display the main menu
    public void DisplayMenu(int arrow) {
        System.out.println("\nWelcome to Laguna Polythenic University Hotel\n");
        System.out.println(arrow == 0 ? "-> Make a Reservation" : "   Make a Reservation");
        System.out.println(arrow == 1 ? "-> View Reservation" : "   View Reservation");
        System.out.println(arrow == 2 ? "-> Exit" : "   Exit");
        clearline(12);  // simulate clear line
        System.out.print("Key Controls ( S = down, W = up, Y = Enter):");
    }
    
    //display the table
    public void DisplayView() {
    	System.out.println("\n");
        db.db_connector();
        int tablelength = db.displayview() + 20;  // Returns the table length
        clearline(tablelength);  // Clear the appropriate number of lines based on the table length
        
    }
    
    public void clearline(int tablelength) {
        int linesToClear = 20 - tablelength;  // get the maximum length of the console that has to be clear
        for (int i = 0; i < linesToClear; i++) {
            System.out.println("\n");//simulate clearline
        }
    }
}




