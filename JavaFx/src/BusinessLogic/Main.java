package BusinessLogic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import DataBase.DataBase;
import Interface.StoreController;

public class Main extends Application 
{
    static final String url = "jdbc:mysql://localhost:3306/project?user=root&password=haris12103";
    static final String username = "root";
    static final String password = "haris12103";

    public static enum State {
		INVENTORY,
		SUPPLIER,
		PLACEORDER,
		CANCELORDER,
        STORE,
        PAYMENT,
        POS,
        RETURN,
        EMPLOYEE
    }
    
    public static State currentState = State.STORE;
    static Connection conn;
    public static void main(String[] args) {
        
        try {
			conn = DataBase.Instance().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
        createTables(conn);
        ArrayList<Book> list = DataBase.Instance().loadBooks();

        Inventory.Instance().setBookList(list);
        System.out.println("Books Loaded successfully");

        launch(args);
        }

    private static void createTables(Connection conn) {
        try {

        	String createPaymentTableSQL = "CREATE TABLE IF NOT EXISTS payments (" +
                    "paymentId INT PRIMARY KEY AUTO_INCREMENT," +
                    "totalAmount DOUBLE," +
                    "details VARCHAR(200)," +
                    "method VARCHAR(20)," +
                    "date DATE)";
            conn.createStatement().execute(createPaymentTableSQL);
            
        	  String createOrderTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                      "orderId VARCHAR(20) PRIMARY KEY," +
                      "supplierId INT," +
                      "totalAmount DOUBLE," +
                      "status VARCHAR(20)," +
                      "paymentId INT," +
                      "FOREIGN KEY (supplierId) REFERENCES suppliers(id)," +
                      "FOREIGN KEY (paymentId) REFERENCES payments(paymentId))";
              conn.createStatement().execute(createOrderTableSQL);

              String createOrderBookTableSQL = "CREATE TABLE IF NOT EXISTS order_book (" +
                      "orderId VARCHAR(20)," +
                      "ISBN VARCHAR(20)," +
                      "quantity INT," +
                      "FOREIGN KEY (orderId) REFERENCES orders(orderId)," +
                      "FOREIGN KEY (ISBN) REFERENCES books(ISBN))";
              conn.createStatement().execute(createOrderBookTableSQL);
              
              String createSaleTableSQL = "CREATE TABLE IF NOT EXISTS sale ("
          			+ "    saleID INT PRIMARY KEY AUTO_INCREMENT,"
          			+ "    total DOUBLE"
          			+ ")";
          	conn.createStatement().execute(createSaleTableSQL);
  
          	String createSaleLineItemTableSQL = "CREATE TABLE IF NOT EXISTS salelineitem ("
          			+ "    saleLineItemID INT PRIMARY KEY AUTO_INCREMENT,"
          			+ "    saleID INT,"
          			+ "    bookISBN VARCHAR(20),"
          			+ "    quantity INT,"
          			+ "    subTotal DOUBLE,"
          			+ "    bookTitle VARCHAR(50),"
          			+ "    FOREIGN KEY (saleID) REFERENCES sale(saleID),"
          			+ "    FOREIGN KEY (bookISBN) REFERENCES books(ISBN)"
          			+ ")";
          	conn.createStatement().execute(createSaleLineItemTableSQL);
  
      	String createReturnsTableSQL = "CREATE TABLE IF NOT EXISTS returns ("
      			+ "    returnID INT PRIMARY KEY AUTO_INCREMENT,"
      			+ "    bookISBN VARCHAR(20),"
      			+ "    saleID INT,"
      			+ "    FOREIGN KEY (bookISBN) REFERENCES books(ISBN),"
      			+ "    FOREIGN KEY (saleID) REFERENCES sale(saleID)"
      			+ ")";
               
        conn.createStatement().execute(createReturnsTableSQL);


         String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                "employeeID VARCHAR(20) PRIMARY KEY," +
                "fullName VARCHAR(50)," +
                "role VARCHAR(50)," +
                "contactInfo VARCHAR(50)" +
                ")";
        
        conn.createStatement().execute(createTableSQL);

        String createTableSQL1 = "CREATE TABLE IF NOT EXISTS schedules (" +
                "scheduleID INT AUTO_INCREMENT PRIMARY KEY," + // Adding auto-increment primary key
                "day VARCHAR(20)," +
                "startTime VARCHAR(20)," +
                "endTime VARCHAR(20)," +
                "employeeID VARCHAR(20)," +
                "FOREIGN KEY (employeeID) REFERENCES employees(employeeID)" +
                ")";

        conn.createStatement().execute(createTableSQL1);

              
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage stage) throws IOException 
    {
     //   FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
    //	FXMLLoader loader = new FXMLLoader(getClass().getResource("Supplier.fxml"));
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interface/MainScreen.fxml"));

        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
