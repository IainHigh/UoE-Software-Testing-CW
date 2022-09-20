package uk.ac.ed.inf;

public class Order {
    public String orderNo;
    public String orderDate;
    public String customer;
    public String creditCardNumber; // Credit card number being stored as a string as it can start with 0.
    public String creditCardExpiry;
    public String cvv;
    public int priceTotalInPence;
    public String[] orderItems;
}
