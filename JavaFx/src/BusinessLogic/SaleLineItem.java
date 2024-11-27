package BusinessLogic;

public class SaleLineItem {
    private String bookISBN;
    private String bookTitle;
    private int quantity;
    private double subTotal;

    // Default constructor
    public SaleLineItem() {
    }

    // Parameterized constructor
    public SaleLineItem(String bookISBN, String bookTitle, int quantity, double price) {
        this.bookISBN = bookISBN;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.subTotal = calculateSubTotal(price);
    }

    // Getters and setters
    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    // Method to calculate subtotal
    private double calculateSubTotal(double pricePerUnit) {
        return pricePerUnit * quantity;
    }

    // Method to update subtotal based on new price per unit
    public void updateSubTotal(double pricePerUnit) {
        this.subTotal = calculateSubTotal(pricePerUnit);
    }

    @Override
    public String toString() {
        return "SaleLineItem{" +
                "bookISBN='" + bookISBN + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", quantity=" + quantity +
                ", subTotal=" + subTotal +
                '}';
    }
}