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

    /**
     * @param participatingRestaurants - Array of participating restaurants (including their menus)
     * @param pizzasOrdered - Variable number of strings for the individual pizzas ordered.
     * @return - The cost in pence of having all these items delivered by drone including the standard delivery
     * charge of £1.
     * @throws InvalidPizzaCombinationException - If a combination where the ordered pizza cannot be delivered by the
     * same restaurant this is an invalid combination.
     */
    public int getDeliveryCost(Restaurant[] participatingRestaurants, String[] pizzasOrdered) throws InvalidPizzaCombinationException {
        int totalCost = 0;
        int count = 0;
        int numberOfPizzasOrdered = pizzasOrdered.length;
        if (numberOfPizzasOrdered > 4){
            throw new InvalidPizzaCombinationException("Too many pizzas ordered");
        }
        // For every restaurant, check if it can complete the order.
        for (Restaurant restaurant : participatingRestaurants) {
            for (Menu menu : restaurant.getMenu()) {
                if (Arrays.asList(pizzasOrdered).contains(menu.name)) {
                    totalCost += menu.priceInPence;
                    count += 1;
                }
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

    static class InvalidPizzaCombinationException extends Throwable {
        public InvalidPizzaCombinationException(String message) {
        }
        public InvalidPizzaCombinationException() {
        }
    }
}
