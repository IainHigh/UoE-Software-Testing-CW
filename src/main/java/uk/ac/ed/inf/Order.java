package uk.ac.ed.inf;

import java.util.Arrays;

public class Order {
    public String orderNo;
    public String orderDate;
    public String customer;
    public String creditCardNumber; // Credit card number being stored as a string as it can start with 0.
    public String creditCardExpiry;
    public String cvv;
    public int priceTotalInPence;
    public String[] orderItems;

    public int getDeliveryCost(Restaurant[] participatingRestaurants, String[] pizzasOrdered) throws InvalidPizzaCombinationException {
        // For every pizza ordered, find the restaurant that sells it and add the price to the total
        int totalCost = 0;
        int count = 0;
        int lengthOfPizzasOrdered = pizzasOrdered.length;
        for (Restaurant restaurant : participatingRestaurants) {
            for (Menu menu : restaurant.getMenu()) {
                if (Arrays.asList(pizzasOrdered).contains(menu.name)) {
                    totalCost += menu.priceInPence;
                    count += 1;
                }
            }
            if (count != 0 && count != lengthOfPizzasOrdered) {
                System.out.println(count);
                throw new InvalidPizzaCombinationException();
            }
        }
        if (count == 0) {
            throw new InvalidPizzaCombinationException();
        }
        return totalCost + 100;
    }

    class InvalidPizzaCombinationException extends Throwable {
    }
}
