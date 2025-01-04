import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DBConnector {
    private String url = "jdbc:mysql://localhost:3306/Restaurant_Reservation";
    private String user = "root";
    private String password = "111104";
    
    Connection con;
    Statement stmt;
    private String query;
    ResultSet res;

    public void db_connector() {
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int displayview() {
        int tablelength = 0;
        try {
            // Print all reservation on the databased
            query = "SELECT * FROM reservations";
            res = stmt.executeQuery(query);
            
            System.out.print("\n\n\nReservation Schedule's\n");
            System.out.println("\n============================================================================================================================================================");
            
            while (res.next()) {
                System.out.print("\nID: " + res.getInt("reservation_id") 
                    + "\nName: " + res.getString("name") 
                    + "\nContact: " + res.getString("contact")
                    + "\nGuest Number: " + res.getInt("num_guests")
                    + "\nDate: " + res.getString("reservation_date")
                	+ "\nSpecial Request: " + res.getString("special_request"));
                System.out.println("\n============================================================================================================================================================");
                
                tablelength++;  // Count the number of reservation on the databased
            }
            System.out.println("\n\n Reservation Schedule: " + tablelength +"/20");
            System.out.println("\nPress Enter to go back to the main menu.");
            con.close();
        } catch (Exception e) {    
            e.printStackTrace();
        }
        
        return tablelength;  // Return the number of records found
    }
}



