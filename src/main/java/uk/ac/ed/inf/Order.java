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

    public int getDeliveryCost(Restaurant[] participatingRestaurants, String[] pizzasOrdered) {
        // For every pizza ordered, find the restaurant that sells it and add the price to the total
        int totalCost = 0;
        for (String pizza : pizzasOrdered) {
            pizzaLoop:
            for (Restaurant restaurant : participatingRestaurants) {
                for (Menu menu : restaurant.getMenu()) {
                    if (menu.name.equals(pizza)) {
                        totalCost += menu.priceInPence;
                        break pizzaLoop;
                    }
                }
            }
        }
        return totalCost + 100;
    }
}
