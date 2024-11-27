package BusinessLogic;

import java.util.List;

public class Supplier {
    private int id;
    private String name;
    private String contactInfo;
    private List<Book> bookList;

    public Supplier(int id, String name, String contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
    }
    
    public Supplier(int id, String name, String contactInfo, List<Book> list) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        bookList = list;
    }

    @Override
    public String toString() {
        return name;  // or any other representation you want in the ComboBox
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}
