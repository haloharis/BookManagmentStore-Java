package BusinessLogic;

import java.util.ArrayList;

public class SupplierOrder {
    private String orderId;
    private Supplier supplier;
    private ArrayList<Book> orderBookList;
    private double totalAmount;
    private String status;
    private Payment payment;

    public SupplierOrder(String orderId, Supplier supplier, Double amount) {
        this.orderId = orderId;
        this.supplier = supplier;
        this.orderBookList = new ArrayList<>();
        this.totalAmount = amount;
        this.status = "Pending"; // Default status
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Supplier ID: ").append(supplier.getId()).append("\n");
        sb.append("Supplier Name: ").append(supplier.getName()).append("\n");
        sb.append("Total Amount: ").append(totalAmount).append("\n");
        sb.append("Status: ").append(status).append("\n");
        
        if (payment!= null) {
            sb.append("Payment Details:\n");
            sb.append("  Payment Method: ").append(payment.getMethod()).append("\n");
            sb.append("  Payment Date: ").append(payment.getDate()).append("\n");
            sb.append("  Payment Amount: ").append(payment.getTotalAmount()).append("\n");
            sb.append("  Account Details: ").append(payment.getDetails()).append("\n");

            sb.append("\n");
        }
        
        sb.append("Books:\n");
        for (Book book : orderBookList) {
            sb.append("  ISBN: ").append(book.getISBN()).append("\n");
            sb.append("  Title: ").append(book.getTitle()).append("\n");
            sb.append("  Author: ").append(book.getAuthor()).append("\n");
            sb.append("  Publisher: ").append(book.getPublisher()).append("\n");
            sb.append("  Price: ").append(book.getPrice()).append("\n");
            sb.append("  Quantity: ").append(book.getQuantity()).append("\n");
            sb.append("  Total Price: ").append(book.getPrice() * book.getQuantity()).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ArrayList<Book> getOrderBookList() {
        return orderBookList;
    }

    public void setOrderBookList(ArrayList<Book> orderBookList) {
        this.orderBookList = orderBookList;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment pay) {

        this.payment = pay;
           }

    public void addBook(Book book) {
        orderBookList.add(book);
    }
}
