package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

/**
 * Record to represent an order.
 * Contains methods to validate the order and assign the order outcome.
 */
public class Order {

    @JsonProperty("orderNo")
    private String orderNo;

    @JsonProperty("orderDate")
    private LocalDate orderDate;

    @JsonProperty("creditCardNumber")
    private String creditCardNumber;

    @JsonProperty("creditCardExpiry")
    private String creditCardExpiry;

    @JsonProperty("cvv")
    private String cvv;

    @JsonProperty("priceTotalInPence")
    private int priceTotalInPence;

    @JsonProperty("orderItems")
    private String[] orderItems;

    @JsonProperty("restaurantOrderedFrom")
    private Restaurant restaurantOrderedFrom;

    public OrderOutcome outcome;

    /**
     * Validates the order and assigns the outcome.
     */
    public void validateOrder(Restaurant[] restaurants) {

        if (restaurants == null || this.orderItems == null) {
            this.outcome = OrderOutcome.Invalid;
        } else if (containsInvalidPizza(restaurants)) {
            // Check if the order contains any pizzas which aren't sold by any restaurant.
            this.outcome = OrderOutcome.InvalidPizzaNotDefined;
        } else if (pizzaOrderedFromMultipleRestaurants(restaurants)) {
            // Check if the order contains pizzas from multiple different restaurants.
            this.outcome = OrderOutcome.InvalidPizzaCombinationMultipleSuppliers;
        } else if (!validCardNumber()) {
            // Check if the credit card number is valid.
            this.outcome = OrderOutcome.InvalidCardNumber;
        } else if (!validCardExpiry()) {
            // Check if the credit card expiry date is valid.
            this.outcome = OrderOutcome.InvalidExpiryDate;
        } else if (!validCVV()) {
            // Check if the cvv is valid.
            this.outcome = OrderOutcome.InvalidCvv;
        } else if (this.orderItems.length == 0 || this.orderItems.length > 4) {
            // Check if the order contains between 1 and 4 pizzas.
            this.outcome = OrderOutcome.InvalidPizzaCount;
        } else if (!validatePriceTotal()) {
            // Check if the total price is correct.
            this.outcome = OrderOutcome.InvalidTotal;
        } else {
            // If all the checks pass, the order is valid.
            this.outcome = OrderOutcome.ValidButNotDelivered;
        }
    }

    /**
     * Checks if the order contains any pizzas which aren't sold by any restaurant.
     *
     * @param participatingRestaurants - Array of participating restaurants (including their menus)
     * @return - True if the order contains any pizzas which aren't sold by any restaurant. False otherwise.
     */
    private boolean containsInvalidPizza(Restaurant[] participatingRestaurants) {
        // Generate a list of all valid pizzas from all restaurants
        String[] validPizzas = Arrays.stream(participatingRestaurants)
                .flatMap(restaurant -> Arrays.stream(restaurant.getMenu()))
                .map(Menu::name)
                .toArray(String[]::new);

        // Check if any of the pizzas ordered are not in the list of valid pizzas
        return Arrays.stream(this.orderItems)
                .anyMatch(pizza -> !Arrays.asList(validPizzas).contains(pizza));
    }

    /**
     * Checks if the order contains pizzas from multiple restaurants.
     * Also sets the restaurantOrderedFrom variable to the restaurant the order was placed with.
     *
     * @param participatingRestaurants - Array of participating restaurants (including their menus)
     * @return - True if the order contains pizzas from multiple restaurants. False otherwise.
     */
    private boolean pizzaOrderedFromMultipleRestaurants(Restaurant[] participatingRestaurants) {
        // Get the restaurant that the first pizza is ordered from
        Restaurant restaurant = Arrays.stream(participatingRestaurants)
                .filter(r -> Arrays.stream(r.getMenu()).anyMatch(menu -> menu.name().equals(this.orderItems[0])))
                .findFirst()
                .orElse(null);

        if (restaurant == null) return true;
        restaurantOrderedFrom = restaurant;

        // Check if any of the pizzas ordered are not from the same restaurant
        return Arrays.stream(this.orderItems)
                .anyMatch(pizza -> Arrays.stream(restaurant.getMenu()).noneMatch(menu -> menu.name().equals(pizza)));
    }

    /**
     * Validates the credit card number of an order.
     *
     * @return True if the credit card number is valid (16 digits, all numbers). False otherwise.
     */
    private boolean validCardNumber() {
        // Card number must be 16 digits long
        if (this.creditCardNumber.length() != 16) return false;

        // Check that all characters are digits
        return this.creditCardNumber.chars().allMatch(Character::isDigit);
    }

    /**
     * Validates the credit card expiration date of an order.
     *
     * @return True if the credit card expiration date is valid (format MM/YY and before order date). False otherwise.
     */
    private boolean validCardExpiry() {
        // Check that the card expiry is in the format MM/YY
        if (!this.creditCardExpiry.matches("\\d{2}/\\d{2}")) return false;

        // Check that the card expiry is after the order date
        int month = Integer.parseInt(this.creditCardExpiry.substring(0, 2));
        int year = 2000 + Integer.parseInt(this.creditCardExpiry.substring(3, 5));

        return (year > this.orderDate.getYear()) || (year == this.orderDate.getYear() && month >= this.orderDate.getMonthValue());
    }

    /**
     * Validates the CVV of an order.
     *
     * @return True if the CVV is valid (3 digits, all numbers), false otherwise.
     */
    private boolean validCVV() {
        if (this.cvv.length() != 3) return false;
        return this.cvv.chars().allMatch(Character::isDigit);
    }

    /**
     * Validates the total price of an order.
     *
     * @return - True if the priceTotalInPence is equal to the sum of the prices of the pizzas ordered. False otherwise.
     */
    private boolean validatePriceTotal() {
        final int FIXED_ORDER_CHARGE = 100;
        int totalCost = 0;
        // For every restaurant menu, check if the menu item is in the pizzas ordered.
        for (Menu menu : this.restaurantOrderedFrom.getMenu()) {
            int numberOfMenuOrder = Collections.frequency(Arrays.asList(this.orderItems), menu.name());
            totalCost += numberOfMenuOrder * menu.priceInPence();
        }
        return (totalCost + FIXED_ORDER_CHARGE == this.priceTotalInPence);
    }

    /**
     * Converts the order to a string in JSON format.
     *
     * @return A String storing the information in JSON format.
     */
    public String toJSON() {
        return "{\"orderNo\": \"" + orderNo + "\""
                + ", \"outcome\": \"" + outcome
                + "\", \"costInPence\": "
                + priceTotalInPence + "}";
    }

    /**
     * Updates the outcome of an order from "ValidButNotDelivered" to "Delivered".
     * Used when the PizzaDrone confirms that the order has been delivered.
     */
    public void setValidOrderToDelivered() {
        if (this.outcome == OrderOutcome.ValidButNotDelivered) {
            this.outcome = OrderOutcome.Delivered;
        } else {
            System.err.println("Order is not valid, cannot be set to delivered.");
        }
    }

    /**
     * Accessor method for the order number.
     *
     * @return The eight-character order number.
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    public boolean isValid() {
        return this.outcome == OrderOutcome.ValidButNotDelivered || this.outcome == OrderOutcome.Delivered;
    }

    /**
     * Accessor method for the order outcome.
     *
     * @return The outcome of the order.
     */
    public Restaurant getRestaurant() {
        return this.restaurantOrderedFrom;
    }
}