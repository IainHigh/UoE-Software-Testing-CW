package uk.ac.ed.inf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class Order {
    public String orderNo;
    public Date orderDate;
    public String customer;
    public String creditCardNumber;
    public String creditCardExpiry;
    public String cvv;
    public int priceTotalInPence;
    public String[] orderItems;
    public Restaurant restaurantOrderedFrom;
    public OrderOutcome outcome;

    /**
     * @param participatingRestaurants - Array of participating restaurants (including their menus)
     * @param pizzasOrdered - Variable number of strings for the individual pizzas ordered.
     * @return - The cost in pence of having all these items delivered by drone including the standard delivery
     * charge of £1.
     * @throws InvalidPizzaCombinationException - If a combination where the ordered pizza cannot be delivered by the
     * same restaurant this is an invalid combination.
     */
    public int getDeliveryCost(Restaurant[] participatingRestaurants, String... pizzasOrdered) throws InvalidPizzaCombinationException {
        if (participatingRestaurants == null || pizzasOrdered == null || pizzasOrdered.length == 0) {
            throw new InvalidPizzaCombinationException("Invalid input");
        }
        int totalCost = 0;
        int count = 0;
        int numberOfPizzasOrdered = pizzasOrdered.length;
        // For every restaurant, check if it can complete the order.
        for (Restaurant restaurant : participatingRestaurants) {
            for (Menu menu : restaurant.getMenu()) {
                int numberOfMenuOrder = Collections.frequency(Arrays.asList(pizzasOrdered), menu.name);
                totalCost += numberOfMenuOrder * menu.priceInPence;
                count += numberOfMenuOrder;
            }
            if (count != 0 && count != numberOfPizzasOrdered) {
                throw new InvalidPizzaCombinationException("All pizzas in an order must be from the same restaurant");
            }
        }
        if (count == 0) {
            throw new InvalidPizzaCombinationException("No pizzas in the order are available from any of the participating restaurants");
        }
        return totalCost + 100;
    }

    public String toJSON() {
        return "{\"orderNo\": \"" + orderNo + "\", \"outcome\": \"" + outcome + "\", \"costInPence\": " + priceTotalInPence + "}";
    }

    public static class InvalidPizzaCombinationException extends Throwable {
    static class InvalidPizzaCombinationException extends Throwable {
        private String message;
        public InvalidPizzaCombinationException(String message) {
            this.message = message;
        }
        public InvalidPizzaCombinationException() {
        }
    }
}
