package BusinessLogic;

public class Payment {
    private Double totalAmount;
    private String details;
    private String method;
    private String Date;

    public Payment() {
        this.totalAmount = 0.0;
        this.details = "";
        method = "Cash";
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void makeCreditCardPayment(String credNum, String name, String expiryDate, String cvv)
    {
    	details = "Credit Number: "+credNum+"\nNamee: "+name+"\nExpiry Date: "+expiryDate+"\nCVV: "+cvv;
    	method="Credit Card";
    	
    }

    public void makeBankTransfer(String accountNum, String name) 
    {
    	details = "Account Number: "+accountNum +"\nName: "+name;
    	method="Bank Transfer";

    }
    
    public String getDate() {
        return Date;
    }
    

    public void setDate(String Date) {
        this.Date = Date;
    }
    
    public void setMethod(String meth) {
        this.method = meth;
    }
    public String getMethod() {
        return method;
    }
    

}