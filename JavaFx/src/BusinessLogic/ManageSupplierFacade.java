package BusinessLogic;

import java.util.ArrayList;
import java.util.List;

import DataBase.DataBase;

public class ManageSupplierFacade {
    private List<Supplier> suppliers;

    public ManageSupplierFacade() 
    {
        suppliers = new ArrayList<>();
        suppliers = DataBase.Instance().getSuppliers();
        for (Supplier supplier : suppliers) {
            supplier.setBookList(DataBase.Instance().getBooksBySupplier(supplier.getId()));
        }
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void addSupplier(int id, String name, String contactInfo, List<Book> bookList) {
        Supplier supplier = new Supplier(id, name, contactInfo, bookList);
        suppliers.add(supplier);
        DataBase.Instance().addSupplier(supplier, bookList);
    }

  

    public void addBookToSupplier(int supplierId, Book book) {
        DataBase.Instance().updateBookToSupplier(supplierId, book);
    }

    public void editSupplier(int id, String name, String contactInfo, List<Book> bookList) {
        Supplier supplier = new Supplier(id, name, contactInfo, bookList);
        DataBase.Instance().updateSupplier(supplier);
    }

    public List<Book> getSupplierBooks(int supplierId) {
        return DataBase.Instance().getBooksBySupplier(supplierId);
    }
    
    public void viewSupplier(Supplier s)
    {
    	
    }
}

