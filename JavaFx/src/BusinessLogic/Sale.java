package BusinessLogic;

import java.util.ArrayList;

public class Sale {
    private ArrayList<SaleLineItem> saleLineItems;
    private double total;

    // Constructor
    public Sale() {
        this.saleLineItems = new ArrayList<>();
        this.total = 0.0;
    }

    // Getters and Setters
    public ArrayList<SaleLineItem> getSaleLineItems() {
        return saleLineItems;
    }

    public void setSaleLineItems(ArrayList<SaleLineItem> saleLineItems) {
        this.saleLineItems = saleLineItems;
        calculateTotal();
    }

    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total)
    {
    	this.total = total;
    }

    // Method to add a SaleLineItem to the sale
    public void addSaleLineItem(SaleLineItem item) {
        this.saleLineItems.add(item);
        calculateTotal();
    }

    // Method to calculate the total price
    public void calculateTotal() {
        double newTotal = 0.0;
        for (SaleLineItem item : saleLineItems) {
            newTotal += item.getSubTotal();
        }
        this.total = newTotal;
    }


    @Override
    public String toString() {
        return "Sale{" +
                "saleLineItems=" + saleLineItems +
                ", total=" + total +
                '}';
    }
    
    
	public void enterBookDetails(String bookISBN, int quantity)
	{
		Book temp = Inventory.Instance().findBook(bookISBN);
		SaleLineItem item = new SaleLineItem(bookISBN, temp.getTitle(), quantity, temp.getPrice());
		this.addSaleLineItem(item);
	}

}