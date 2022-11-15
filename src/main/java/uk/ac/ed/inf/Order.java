package uk.ac.ed.inf;

import java.time.*;
import java.util.*;

public class Order {
    public String orderNo;
    public LocalDate orderDate;
    public String creditCardNumber;
    public String creditCardExpiry;
    public String cvv;
    public int priceTotalInPence;
    public String[] orderItems;
    public Restaurant restaurantOrderedFrom;
    public OrderOutcome outcome;

    /**
     * Validates the order and assigns the outcome.
     * @return An OrderOutcome enum value depending on the validity of the order.
     */
    public OrderOutcome validateOrder(Restaurant[] participatingRestaurants) {
        if (!validCardNumber(this.creditCardNumber)) return OrderOutcome.INVALID_CARD_NUMBER;
        if (!validCardExpiry(this.creditCardExpiry, this.orderDate)) return OrderOutcome.INVALID_EXPIRY_DATE;
        if (!validCVV(this.cvv)) return OrderOutcome.INVALID_CVV;
        if (this.orderItems.length == 0 || this.orderItems.length > 4) return OrderOutcome.INVALID_PIZZA_COUNT;
        try {
            int calculatedTotal = this.getDeliveryCost(participatingRestaurants, this.orderItems);
            if (calculatedTotal != this.priceTotalInPence) return OrderOutcome.INVALID_TOTAL;
        }
        catch (Order.InvalidPizzaCombinationException e) {
            if (Objects.equals(e.getMessage(), "Pizzas cannot be ordered from different restaurants")) {
                return OrderOutcome.INVALID_PIZZA_COMBINATION_MULTIPLE_SUPPLIERS;
            }
            if (Objects.equals(e.getMessage(), "Invalid pizza ordered")) {
                return OrderOutcome.INVALID_PIZZA_NOT_DEFINED;
            }
            return OrderOutcome.INVALID;
        }
        return OrderOutcome.VALID_BUT_NOT_DELIVERED;
    }

    /**
     * Validates the credit card number of an order.
     * @param cardNumber The credit card number.
     * @return True if the credit card number is valid (16 digits, all numbers), false otherwise.
     */
    private boolean validCardNumber(String cardNumber) {
        // Card number must be 16 digits long
        if (cardNumber.length() != 16) return false;

        // Check that all characters are digits
        return cardNumber.chars().allMatch(Character::isDigit);
    }

    /**
     * Validates the credit card expiration date of an order.
     * @param cardExpiry The credit card expiration date.
     * @param orderDate The date the order was placed.
     * @return True if the credit card expiration date is valid (format MM/YY and before the order date), false
     * otherwise.
     */
    private boolean validCardExpiry(String cardExpiry, LocalDate orderDate) {
        // Check that the card expiry is in the format MM/YY
        if (!cardExpiry.matches("\\d{2}/\\d{2}")) return false;

        // Check that the card expiry is after the order date
        int month = Integer.parseInt(cardExpiry.substring(0, 2));
        int year = 2000 + Integer.parseInt(cardExpiry.substring(3, 5));

        return (year > orderDate.getYear()) || (year == orderDate.getYear() && month >= orderDate.getMonthValue());
    }

    /**
     * Validates the CVV of an order.
     * @param cvv The CVV.
     * @return True if the CVV is valid (3 digits, all numbers), false otherwise.
     */
    private boolean validCVV(String cvv) {
        if (cvv.length() != 3) return false;
        return cvv.chars().allMatch(Character::isDigit);
    }

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
            for (Restaurant.Menu menu : restaurant.getMenu()) {
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

    /**
     * @param participatingRestaurants - Array of participating restaurants (including their menus)
     * @param pizzasOrdered - Variable number of strings for the individual pizzas ordered.
     * @return - True if the order contains any pizzas which aren't sold by any restaurant.
     */
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
        return "{\"orderNo\": \"" + orderNo + "\""
                + ", \"outcome\": \"" + outcome
                + "\", \"costInPence\": "
                + priceTotalInPence + "}";
    }
    public static class InvalidPizzaCombinationException extends Throwable {
        public InvalidPizzaCombinationException(String message) {
            super(message);
        }
    }
}
