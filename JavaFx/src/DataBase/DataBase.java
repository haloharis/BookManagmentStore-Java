package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import BusinessLogic.Book;
import BusinessLogic.Employee;
import BusinessLogic.Payment;
import BusinessLogic.Return;
import BusinessLogic.Sale;
import BusinessLogic.SaleLineItem;
import BusinessLogic.Schedule;
import BusinessLogic.Supplier;
import BusinessLogic.SupplierOrder;

public class DataBase {
    private static DataBase instance;
    private Connection connection;

    public static DataBase Instance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    private DataBase() {
        getConnection();
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                // Specify the database name (schema)
                String url = "jdbc:mysql://localhost:3306/project?user=root&password=haris12103";
                String username = "root";
                String password = "haris12103";
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected to database successfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


    public void addBook(Book book) {
        try {
            String query = "INSERT INTO books (ISBN, title, author, publisher, quantity, price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, book.getISBN());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getPublisher());
            statement.setInt(5, book.getQuantity());
            statement.setDouble(6, book.getPrice());
            System.out.println("Book added");
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeBook(String ISBN) {
        try {
            String query = "DELETE FROM books WHERE ISBN = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ISBN);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        try {
            String query = "UPDATE books SET title = ?, author = ?, publisher = ?, quantity = ?, price = ? WHERE ISBN = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setInt(4, book.getQuantity());
            statement.setDouble(5, book.getPrice());
            statement.setString(6, book.getISBN());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();
        try {
            String query = "SELECT * FROM books";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(
                    resultSet.getString("ISBN"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("publisher"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price")
                );
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Supplier> getSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT id, name, contactInfo FROM suppliers";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String contactInfo = resultSet.getString("contactInfo");
                suppliers.add(new Supplier(id, name, contactInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<Book> getBooksBySupplier(int supplierId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.ISBN, b.title, b.author, b.publisher, b.quantity, b.price " +
                       "FROM books b " +
                       "JOIN supplier_books sb ON b.ISBN = sb.book_isbn " +
                       "WHERE sb.supplier_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, supplierId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String ISBN = resultSet.getString("ISBN");
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String publisher = resultSet.getString("publisher");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    books.add(new Book(ISBN, title, author, publisher, quantity, price));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public void addSupplier(Supplier supplier, List<Book> bookList) {
        try {
            String query = "INSERT INTO suppliers (id, name, contactInfo) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, supplier.getId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getContactInfo());
            statement.executeUpdate();
            
            for (Book book : bookList) {
                addBookToSupplier(supplier.getId(), book);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addBookToSupplier(int supplierId, Book book) {
        try {
            String query = "INSERT INTO supplier_books (supplier_id, book_isbn) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, supplierId);
            statement.setString(2, book.getISBN());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeSupplier(int id) {
        try {
            String query = "DELETE FROM suppliers WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            
            // Also delete the supplier's books from the supplier_books table
            query = "DELETE FROM supplier_books WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addSupplierOrder(SupplierOrder supplierOrder) {
        try {
            String query = "INSERT INTO orders (orderId, supplierId, totalAmount, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, supplierOrder.getOrderId());
            statement.setInt(2, supplierOrder.getSupplier().getId());
            statement.setDouble(3, supplierOrder.getTotalAmount());
            statement.setString(4, supplierOrder.getStatus());
            statement.executeUpdate();

            // Add books of the order to the order_books table
            for (Book book : supplierOrder.getOrderBookList()) {
                addOrderBook(supplierOrder.getOrderId(), book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrderBook(String orderId, Book book) {
        try {
            String query = "INSERT INTO order_book (orderId, ISBN, quantity) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, orderId);
            statement.setString(2, book.getISBN());
            statement.setInt(3, book.getQuantity());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SupplierOrder> getSupplierOrders() {
        List<SupplierOrder> supplierOrders = new ArrayList<>();
        try {
            String query = "SELECT o.orderId, o.supplierId, o.totalAmount, o.status, p.paymentId, p.totalAmount as paymentTotalAmount, p.details, p.method, p.date " +
                           "FROM orders o " +
                           "LEFT JOIN payments p ON o.paymentId = p.paymentId";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String orderId = resultSet.getString("orderId");
                int supplierId = resultSet.getInt("supplierId");
                double totalAmount = resultSet.getDouble("totalAmount");
                String status = resultSet.getString("status");

                // Retrieve supplier information
                Supplier supplier = getSupplierById(supplierId);

                // Retrieve order books
                ArrayList<Book> orderBooks = getOrderBooksByOrderId(orderId);

                // Create SupplierOrder object
                SupplierOrder supplierOrder = new SupplierOrder(orderId, supplier, totalAmount);
                supplierOrder.setTotalAmount(totalAmount);
                supplierOrder.setStatus(status);
                supplierOrder.setOrderBookList(orderBooks);

                // Retrieve payment information
                int paymentId = resultSet.getInt("paymentId");
                double paymentTotalAmount = resultSet.getDouble("paymentTotalAmount");
                String paymentDetails = resultSet.getString("details");
                String paymentMethod = resultSet.getString("method");
                String paymentDate = resultSet.getString("date");

                Payment payment = new Payment();
                payment.setTotalAmount(paymentTotalAmount);
                payment.setDetails(paymentDetails);
                payment.setMethod(paymentMethod);
                payment.setDate(paymentDate);

                supplierOrder.setPayment(payment);

                supplierOrders.add(supplierOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplierOrders;
    }

    private Supplier getSupplierById(int supplierId) {
        String query = "SELECT id, name, contactInfo FROM suppliers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, supplierId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String contactInfo = resultSet.getString("contactInfo");
                return new Supplier(supplierId, name, contactInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Book> getOrderBooksByOrderId(String orderId) {
        ArrayList<Book> orderBooks = new ArrayList<>();
        String query = "SELECT b.ISBN, b.title, b.author, b.publisher, b.quantity, b.price " +
                       "FROM order_book ob " +
                       "JOIN books b ON ob.ISBN = b.ISBN " +
                       "WHERE ob.orderId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String publisher = resultSet.getString("publisher");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                Book book = new Book(ISBN, title, author, publisher, quantity, price);
                orderBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderBooks;
    }

    public void updateOrderStatus(String orderId, String status) {
        try {
            String query = "UPDATE orders SET status = ? WHERE orderId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, status);
            statement.setString(2, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateSupplierOrderPayment(SupplierOrder supplierOrder, Payment payment) {
        try {
            int paymentId = insertPayment(payment);
            if (paymentId > 0) {
                String updateSQL = "UPDATE orders SET paymentId = ? WHERE orderId = ?";
                PreparedStatement pstmt = connection.prepareStatement(updateSQL);
                pstmt.setInt(1, paymentId);
                pstmt.setString(2, supplierOrder.getOrderId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int insertPayment(Payment payment) {
        String insertSQL = "INSERT INTO payments (totalAmount, details, method, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, payment.getTotalAmount());
            pstmt.setString(2, payment.getDetails());
            pstmt.setString(3, payment.getMethod());
            pstmt.setString(4, payment.getDate());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void updateBookToSupplier(int supplierId, Book book) {
        addBook(book);
        String query = "INSERT INTO supplier_books (supplier_id, book_isbn) VALUES (?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, supplierId);
            statement.setString(2, book.getISBN());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier supplier) {
        String query = "UPDATE suppliers SET name =?, contactInfo =? WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getContactInfo());
            statement.setInt(3, supplier.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Delete existing books of the supplier
        String deleteQuery = "DELETE FROM supplier_books WHERE supplier_id =?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, supplier.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Add new books of the supplier
        for (Book book : supplier.getBookList()) {
            addBookToSupplier(supplier.getId(), book);
        }
    }
    
    public void addSale(Sale sale) {
        try {
            // Insert into sales table
            String saleQuery = "INSERT INTO sale (total) VALUES (?)";
            PreparedStatement saleStatement = connection.prepareStatement(saleQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            saleStatement.setDouble(1, sale.getTotal());
            saleStatement.executeUpdate();
            
            // Get generated sale ID
            ResultSet generatedKeys = saleStatement.getGeneratedKeys();
            int saleId = -1;
            if (generatedKeys.next()) {
                saleId = generatedKeys.getInt(1);
            }

            // Insert into sale_line_items table
            String lineItemQuery = "INSERT INTO salelineitem (saleID, bookISBN, quantity, subTotal, bookTitle) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement lineItemStatement = connection.prepareStatement(lineItemQuery);

            for (SaleLineItem item : sale.getSaleLineItems()) {
                lineItemStatement.setInt(1, saleId);
                lineItemStatement.setString(2, item.getBookISBN());
                lineItemStatement.setInt(3, item.getQuantity());
                lineItemStatement.setDouble(4, item.getSubTotal());
                lineItemStatement.setString(5, item.getBookTitle());
                lineItemStatement.addBatch();
            }

            lineItemStatement.executeBatch();

            System.out.println("Sale added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Sale findSale(int saleId) {
        Sale sale = null;
        ArrayList<SaleLineItem> saleLineItems = new ArrayList<>();

        try {
            // Fetch the sale from the sale table
            String saleQuery = "SELECT saleID, total FROM sale WHERE saleID = ?";
            PreparedStatement saleStatement = connection.prepareStatement(saleQuery);
            saleStatement.setInt(1, saleId);
            ResultSet saleResultSet = saleStatement.executeQuery();

            if (saleResultSet.next()) {
                double total = saleResultSet.getDouble("total");

                // Create the Sale object
                sale = new Sale();
                sale.setTotal(total);

                // Fetch the sale line items from the salelineitem table
                String saleLineItemQuery = "SELECT saleLineItemID, saleID, bookISBN, quantity, subTotal, bookTitle FROM salelineitem WHERE saleID = ?";
                PreparedStatement saleLineItemStatement = connection.prepareStatement(saleLineItemQuery);
                saleLineItemStatement.setInt(1, saleId);
                ResultSet saleLineItemResultSet = saleLineItemStatement.executeQuery();

                while (saleLineItemResultSet.next()) {
                    int saleLineItemID = saleLineItemResultSet.getInt("saleLineItemID");
                    String bookISBN = saleLineItemResultSet.getString("bookISBN");
                    int quantity = saleLineItemResultSet.getInt("quantity");
                    double subTotal = saleLineItemResultSet.getDouble("subTotal");
                    String bookTitle = saleLineItemResultSet.getString("bookTitle");

                    // Create SaleLineItem object
                    SaleLineItem saleLineItem = new SaleLineItem();
                    saleLineItem.setBookISBN(bookISBN);
                    saleLineItem.setBookTitle(bookTitle);
                    saleLineItem.setQuantity(quantity);
                    saleLineItem.setSubTotal(subTotal);

                    // Add the SaleLineItem to the list
                    saleLineItems.add(saleLineItem);
                }

                // Set the saleLineItems to the Sale object
                sale.setSaleLineItems(saleLineItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sale;
    }

    public List<String> getAllSaleIDs() {
        List<String> saleIDs = new ArrayList<>();
        String query = "SELECT saleID FROM sale";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                saleIDs.add(String.valueOf(resultSet.getInt("saleID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saleIDs;
    
    }
    
    public void addReturn(Return returnObj) {
        try {
            String query = "INSERT INTO returns (bookISBN, saleID) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, returnObj.getBookISBN());
            statement.setInt(2, returnObj.getSaleID());
            statement.executeUpdate();
            System.out.println("Return added to the database successfully.");
        } catch (Exception e) {
            System.err.println("Error adding return to the database: " + e.getMessage());
        }
    }

    public void addEmployee(Employee employee){
        String insertEmployeeSQL = "INSERT INTO employees (employeeID, fullName, role, contactInfo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertEmployeeSQL)) {
            pstmt.setString(1, employee.getEmployeeID());
            pstmt.setString(2, employee.getFullName());
            pstmt.setString(3, employee.getRole());
            pstmt.setString(4, employee.getContactInfo());
            
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println("Error adding employee to database: " + e.getMessage());
        }
    }

    public void removeEmployee(String employeeID) {
        String deleteEmployeeSQL = "DELETE FROM employees WHERE employeeID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteEmployeeSQL)) {
            pstmt.setString(1, employeeID);

            pstmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println("Error removing employee to database: " + e.getMessage());
        }
    }

    public void updateEmployee(Employee updatedEmployee) {
        String updateEmployeeSQL = "UPDATE employees SET fullName = ?, role = ?, contactInfo = ? WHERE employeeID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateEmployeeSQL)) {
            pstmt.setString(1, updatedEmployee.getFullName());
            pstmt.setString(2, updatedEmployee.getRole());
            pstmt.setString(3, updatedEmployee.getContactInfo());
            pstmt.setString(4, updatedEmployee.getEmployeeID());

            pstmt.executeUpdate();
            System.out.println("Employee updated in the database successfully.");
        } catch (Exception e) {
            System.err.println("Error updating employee in the database: " + e.getMessage());
        }
    }

    public ArrayList<Employee> getEmployees() {
        String selectEmployeesSQL = "SELECT employeeID, fullName, role, contactInfo FROM employees";
        ArrayList<Employee> employeeList = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(selectEmployeesSQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String employeeID = rs.getString("employeeID");
                String fullName = rs.getString("fullName");
                String role = rs.getString("role");

                Employee employee = new Employee(employeeID, fullName, role);
                employeeList.add(employee);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving employees from the database: " + e.getMessage());
        }

        return employeeList;
    }
    
    public void addEmployeeSchedule(Employee employeeToUpdate, Schedule newSchedule) {
        String insertScheduleSQL = "INSERT INTO schedules (day, startTime, endTime, employeeID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertScheduleSQL)) {
            pstmt.setString(1, newSchedule.getDay());
            pstmt.setString(2, newSchedule.getStartTime());
            pstmt.setString(3, newSchedule.getEndTime());
            pstmt.setString(4, employeeToUpdate.getEmployeeID());
            pstmt.executeUpdate();
            System.out.println("Schedule added for employee successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding schedule for employee: " + e.getMessage());
        }
    }

     public List<Schedule> getEmployeeSchedule(Employee employee) {
        List<Schedule> employeeSchedule = new ArrayList<>();
        String selectScheduleSQL = "SELECT * FROM schedules WHERE employeeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectScheduleSQL)) {
            pstmt.setString(1, employee.getEmployeeID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");
                Schedule schedule = new Schedule(day, startTime, endTime);
                employeeSchedule.add(schedule);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employee schedule: " + e.getMessage());
        }
        return employeeSchedule;
    }


    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        String selectEmployeesSQL = "SELECT * FROM employees";
        try (PreparedStatement pstmt = connection.prepareStatement(selectEmployeesSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String employeeID = rs.getString("employeeID");
                String fullName = rs.getString("fullName");
                String role = rs.getString("role");
                String contactInfo = rs.getString("contactInfo");
                ArrayList<Schedule> schedules = getEmployeeSchedule(employeeID);
                Employee employee = new Employee(employeeID, fullName, role, contactInfo, schedules);
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employees with schedules: " + e.getMessage());
        }
        return employees;
    }

    private ArrayList<Schedule> getEmployeeSchedule(String employeeID) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        String selectSchedulesSQL = "SELECT * FROM schedules WHERE employeeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSchedulesSQL)) {
            pstmt.setString(1, employeeID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");
                Schedule schedule = new Schedule(day, startTime, endTime);
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving schedules for employee " + employeeID + ": " + e.getMessage());
        }
        return schedules;
    }

}
