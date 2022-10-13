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

        // Check if the input is valid
        if (participatingRestaurants == null || pizzasOrdered == null || pizzasOrdered.length == 0) {
            throw new InvalidPizzaCombinationException("Invalid input");
        }

        // Check if the order contains any pizzas which aren't sold by any restaurant.
        if (containsInvalidPizza(participatingRestaurants, pizzasOrdered)) {
            throw new InvalidPizzaCombinationException("Invalid pizza ordered");
        }

        int totalCost = 0;
        int count = 0;
        // For every restaurant menu, check if the menu item is in the pizzas ordered.
        for (Restaurant restaurant : participatingRestaurants) {
            for (Menu menu : restaurant.getMenu()) {
                int numberOfMenuOrder = Collections.frequency(Arrays.asList(pizzasOrdered), menu.name);
                totalCost += numberOfMenuOrder * menu.priceInPence;
                count += numberOfMenuOrder;
            }
            if (count != 0)  {
                if (count != pizzasOrdered.length){
                    throw new InvalidPizzaCombinationException("Pizzas cannot be ordered from different restaurants");
                }
                restaurantOrderedFrom = restaurant;
                break;
            }
        }
        return totalCost + 100;
    }

    private boolean containsInvalidPizza(Restaurant[] participatingRestaurants, String... pizzasOrdered) {
        // Generate a list of all valid pizzas from all restaurants
        String[] validPizzas = Arrays.stream(participatingRestaurants)
                .flatMap(restaurant -> Arrays.stream(restaurant.getMenu()))
                .map(menu -> menu.name)
                .toArray(String[]::new);

        // Check if any of the pizzas ordered are not in the list of valid pizzas
        return Arrays.stream(pizzasOrdered)
                .anyMatch(pizza -> !Arrays.asList(validPizzas).contains(pizza));
    }

    public String toJSON() {
        return "{\"orderNo\": \"" + orderNo + "\", \"outcome\": \"" + outcome + "\", \"costInPence\": " + priceTotalInPence + "}";
    }
    public class InvalidPizzaCombinationException extends Throwable {
        public InvalidPizzaCombinationException(String message) {
            super(message);
        }
        public InvalidPizzaCombinationException() {
            super();
        }
    }
}
