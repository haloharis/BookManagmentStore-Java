package BusinessLogic;

import java.util.ArrayList;

import DataBase.DataBase;

public class Inventory {
    private ArrayList<Book> bookList;
    private int level;
    private static Inventory instance;

    private Inventory() {
        bookList = new ArrayList<>();
        level = 0;
        bookList.addAll(DataBase.Instance().loadBooks()); // Load books from the database initially
    }

    public static Inventory Instance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public ArrayList<Book> getBookList() {
        return bookList;
    }

    public void display() {
        for (Book book : bookList) {
            book.display();
        }
    }
    
    public void setBookList(ArrayList<Book> list)
    {
    	bookList = list;
    }

    public Book addBook(String isbn, String title, String author, String publisher, int quantity, double price) 
    {
        Book newBook = new Book(isbn, title, author, publisher, quantity, price);
        if (findBook(isbn)!= null) {
            return null;
        }

        bookList.add(newBook);
        DataBase.Instance().addBook(newBook);
        return newBook;

    }

    public void remove(String ISBN) {
        bookList.removeIf(book -> book.getISBN().equals(ISBN));
        DataBase.Instance().removeBook(ISBN);
    }

    public void updateBook(Book book)
    {
    	DataBase.Instance().updateBook(book);   
    }

    public Book findBook(String ISBN) {
        for (Book book : bookList) 
        {
            if (book.getISBN().equals(ISBN)) 
            {
                return book;
            }
        }
        return null;
    }

    public void modifyLevel(int newLevel) {
        this.level = newLevel;
    }

    public void setLevel(int newLevel) {
        this.level = newLevel;
    }

    public int getLevel() {
        return level;
    }

    public void viewInventory() {
        for (Book book : bookList) {
            book.display();
        }
    }
    
    public void AddBookQuantity(String bookISBN, int quantity)
    {
    	Book b = findBook(bookISBN);
    	b.setQuantity(b.getQuantity() + quantity);
    }
    
    
}
