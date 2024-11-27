package BusinessLogic;

public class Return {
    private String bookISBN;
    private int saleID;

    // Constructor
    public Return(String bookISBN, int saleID) {
        this.bookISBN = bookISBN;
        this.saleID = saleID;
    }

    // Getters and setters
    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }
}