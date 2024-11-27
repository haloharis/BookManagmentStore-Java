package BusinessLogic;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import DataBase.DataBase;

public class ManageInventoryFacade {
	 private ArrayList<SupplierOrder> supplierOrders = new ArrayList<>();
	    private int nextOrderId = 0; // Starting order ID

	    public ManageInventoryFacade() 
	    {
	        supplierOrders = (ArrayList<SupplierOrder>) DataBase.Instance().getSupplierOrders();
	    }
    public void viewInventory() 
    {
        Inventory.Instance().viewInventory();
    }

    public ArrayList<SupplierOrder> getOrderlist()
    {
    	return supplierOrders;
    }
    public void updateInventory() {
    }

    public Book addNewBook(String isbn, String title, String author, String publisher, int quantity, double price) 
    {
      return Inventory.Instance().addBook(isbn, title, author, publisher, quantity, price);
    }

    public void removeBook(Book book) {
        Inventory.Instance().remove(book.getISBN());
    }

    public void modifyLevels(int newLevel) {
        Inventory.Instance().modifyLevel(newLevel);
    }
    
    public void updateBook(Book book) 
    {
        Book existingBook = Inventory.Instance().findBook(book.getISBN());
        if (existingBook != null) 
        {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPublisher(book.getPublisher());
            existingBook.setQuantity(book.getQuantity());
            existingBook.setPrice(book.getPrice());
            
            Inventory.Instance().updateBook(existingBook);
            DataBase.Instance().updateBook(book);
        }
    }
    

    public void addBookToOrder(Book book, int quantity, Supplier supplier) {
        SupplierOrder currentOrder = getCurrentOrder();
        if (currentOrder == null || !currentOrder.getSupplier().equals(supplier)) {
            currentOrder = new SupplierOrder("SO" + nextOrderId++, supplier, 0.0);
            supplierOrders.add(currentOrder);
        }
        for (int i = 0; i < quantity; i++) {
            currentOrder.addBook(book);
        }
    }

    SupplierOrder getCurrentOrder() {
        if (!supplierOrders.isEmpty()) {
            return supplierOrders.get(supplierOrders.size() - 1);
        }
        return null;
    }

    
    public int assignOrderId() {
        int maxOrderId = 0;
        for (SupplierOrder order : supplierOrders) {
            int orderId = Integer.parseInt(order.getOrderId());
            if (orderId > maxOrderId) {
                maxOrderId = orderId;
            }
        }
        nextOrderId = maxOrderId + 1;
        return nextOrderId;
    }

    
    public void placeOrder(Supplier supplier, List<Book> orderedBooks, Double amount) {
        int orderId = assignOrderId(); 

        SupplierOrder supplierOrder = new SupplierOrder(String.valueOf(orderId), supplier, amount);
        
        for (Book book : orderedBooks) {
            Book bookToAdd = new Book(book.getISBN(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getQuantity(), book.getPrice());
            supplierOrder.addBook(bookToAdd);
        }


        supplierOrders.add(supplierOrder);

        DataBase.Instance().addSupplierOrder(supplierOrder);
    }

    public void cancelOrder(String orderId) 
    {
    	for (SupplierOrder order : supplierOrders) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus("Cancelled");
                break;
            }
        }
        DataBase.Instance().updateOrderStatus(orderId, "Cancelled");
        

    }
    
    public SupplierOrder getLastSupplierOrder() {
        if (supplierOrders.size() > 0) {
            Collections.sort(supplierOrders, Comparator.comparingInt(order -> Integer.parseInt(order.getOrderId())));

            return supplierOrders.get(supplierOrders.size() - 1);
        } else {
            return null;
        }
    }
    public void confirmPayment(SupplierOrder supplierOrder, Payment payment) {
        DataBase.Instance().updateSupplierOrderPayment(supplierOrder, payment);
    }
}
