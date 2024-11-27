package BusinessLogic;

import DataBase.DataBase;

public class RegisterFacade {
	
	private Sale currentSale = null;
	public void initializeSale()
	{
		currentSale = new Sale();
	}
	
	public void enterBookDetails(String bookISBN, int quantity)
	{
		currentSale.enterBookDetails(bookISBN, quantity);
		Inventory.Instance().AddBookQuantity(bookISBN, -(quantity));
	}
	
	public void endSale()
	{
		currentSale.calculateTotal();
		DataBase.Instance().addSale(currentSale);
		currentSale = null;
	}
	
	public Sale getSale()
	{
		return currentSale;
	}
	
	public Sale ViewSaleDetails(int saleid)
	{
		Sale s = DataBase.Instance().findSale(saleid);
		return s;
	}
	
	public void returnBook(String isbn, int quantity,int saleID)
	{
		Return r = new Return(isbn, saleID);
		DataBase.Instance().addReturn(r);
		Inventory.Instance().AddBookQuantity(isbn, quantity);
	}

}