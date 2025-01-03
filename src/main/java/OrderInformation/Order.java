package OrderInformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Character.isDigit;

/**
 * Class to represent an order.
 * Contains methods to validate the order and assign the order outcome.
 */
public class Order {

    @JsonProperty("orderNo")
    private String orderNo;

    @JsonProperty("orderDate")
    private String orderDate;

    @JsonProperty("creditCardInformation")
    private CreditCardInformation creditCardInformation;

    @JsonProperty("priceTotalInPence")
    private int priceTotalInPence;

    @JsonProperty("pizzasInOrder")
    private Pizza[] pizzasInOrder;

    @JsonProperty("restaurantOrderedFrom")
    private Restaurant restaurantOrderedFrom;

    private OrderOutcome outcome;

    @JsonCreator
    public Order(
            @JsonProperty("orderNo") String orderNo,
            @JsonProperty("orderDate") String orderDate,
            @JsonProperty("creditCardInformation") CreditCardInformation creditCardInformation,
            @JsonProperty("priceTotalInPence") int priceTotalInPence,
            @JsonProperty("pizzasInOrder") Pizza[] pizzasInOrder) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.creditCardInformation = creditCardInformation;
        this.priceTotalInPence = priceTotalInPence;
        this.pizzasInOrder = pizzasInOrder;
    }

    /**
     * Validates the order and assigns the order outcome.
     *
     * @param restaurants The array of restaurants used to validate the order.
     * @param date        The date which was passed into the command line arguments.
     */
    public void validateOrder(Restaurant[] restaurants, String date) {
        if (!this.orderDate.matches("\\d{4}-\\d{2}-\\d{2}") || !this.orderDate.equals(date)) {
            // The orderDate must be in YYYY-MM-DD format and be the same as the date given
            // in the command line.
            this.outcome = OrderOutcome.Invalid;
        } else if (!orderNo.chars().allMatch(c -> isDigit(c) || (c >= 'A' && c <= 'F')) || orderNo.length() != 8) {
            // The orderNo must be an 8 digit hexadecimal number.
            this.outcome = OrderOutcome.Invalid;
        } else if (restaurants == null || this.pizzasInOrder == null || this.pizzasInOrder.length == 0) {
            // There must be restaurants and pizzasInOrder.
            this.outcome = OrderOutcome.Invalid;
        } else if (containsInvalidPizza(restaurants)) {
            // Check if the order contains any pizzas which aren't sold by any restaurant.
            this.outcome = OrderOutcome.InvalidPizzaNotDefined;
        } else if (pizzaOrderedFromMultipleRestaurants(restaurants)) {
            // Check if the order contains pizzas from multiple different restaurants.
            this.outcome = OrderOutcome.InvalidPizzaCombinationMultipleSuppliers;
        } else if (!this.creditCardInformation.validCardNumber()) {
            // Check if the credit card number is valid.
            this.outcome = OrderOutcome.InvalidCardNumber;
        } else if (!this.creditCardInformation.validExpiryDate() || !orderBeforeCardExpiration()) {
            // Check if the credit card expiry date is valid and the order is placed before
            // the card expires.
            this.outcome = OrderOutcome.InvalidExpiryDate;
        } else if (!this.creditCardInformation.validCVV()) {
            // Check if the cvv is valid.
            this.outcome = OrderOutcome.InvalidCvv;
        } else if (this.pizzasInOrder.length == 0 || this.pizzasInOrder.length > 4) {
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
     * @param participatingRestaurants Array of participating restaurants (including
     *                                 their menus)
     * @return True if the order contains any pizzas which aren't sold by any
     *         restaurant. False otherwise.
     */
    private boolean containsInvalidPizza(Restaurant[] participatingRestaurants) {
        // Generate a list of all valid pizzas from all restaurants.
        List<String> validPizzas = Arrays.stream(participatingRestaurants)
                .flatMap(restaurant -> Arrays.stream(restaurant.getMenu()))
                .map(Menu::getName)
                .collect(Collectors.toList());

        // Check if any of the pizzas ordered are not in the list of valid pizzas.
        return Arrays.stream(this.pizzasInOrder)
                .map(Pizza::getName)
                .anyMatch(pizza -> !validPizzas.contains(pizza));
    }

    /**
     * Checks if the order contains pizzas from multiple restaurants.
     * Also sets the restaurantOrderedFrom variable to the restaurant the order was
     * placed with.
     *
     * @param participatingRestaurants Array of participating restaurants (including
     *                                 their menus)
     * @return True if the order contains pizzas from multiple restaurants. False
     *         otherwise.
     */
    private boolean pizzaOrderedFromMultipleRestaurants(Restaurant[] participatingRestaurants) {
        // Get the restaurant that the first pizza is ordered from.
        Restaurant restaurant = Arrays.stream(participatingRestaurants)
                .filter(r -> Arrays.stream(r.getMenu())
                        .anyMatch(menu -> menu.getName().equals(this.pizzasInOrder[0].getName())))
                .findFirst()
                .orElse(null);

        if (restaurant == null)
            return true;
        restaurantOrderedFrom = restaurant;

        // Check if any of the pizzas ordered are not from the same restaurant.
        return Arrays.stream(this.pizzasInOrder)
                .map(Pizza::getName)
                .anyMatch(pizza -> Arrays.stream(restaurant.getMenu())
                        .noneMatch(menu -> menu.getName().equals(pizza)));
    }

    /**
     * Validates the credit card expiration date of an order.
     *
     * @return True if the credit card expiration date is valid (format MM/YY and
     *         before order date). False otherwise.
     */
    private boolean orderBeforeCardExpiration() {

        LocalDate date = LocalDate.parse(this.orderDate);

        // Since credit cards only measure the year in the last two digits, we need to
        // add 2000 to the year.
        final int CENTURY = 2000;

        // Check that the card expiry is in the format MM/YY
        String expiry = this.creditCardInformation.getCreditCardExpiry();
        if (!expiry.matches("\\d{2}/\\d{2}"))
            return false;

        // Check that the card expiry is after the order date.
        int month = Integer.parseInt(expiry.substring(0, 2));
        int year = CENTURY + Integer.parseInt(expiry.substring(3, 5));

        return (year > date.getYear()) || (year == date.getYear() && month >= date.getMonthValue());
    }

    /**
     * Validates the total price of an order.
     *
     * @return True if the priceTotalInPence is equal to the sum of the prices of
     *         the pizzas ordered. False otherwise.
     */
    private boolean validatePriceTotal() {
        final int FIXED_ORDER_CHARGE = 100;
        int totalCost = 0;
        // For every restaurant menu, check if the menu item is in the pizzas ordered.
        // We've already validated that the pizzas are ordered from the same restaurant
        // and all pizzas are valid.
        for (Menu menu : this.restaurantOrderedFrom.getMenu()) {
            int numberOfMenuOrder = (int) Arrays.stream(this.pizzasInOrder)
                    .filter(pizza -> menu.getName().equals(pizza.getName()))
                    .count();
            totalCost += numberOfMenuOrder * menu.getPriceInPence();
        }
        return (totalCost + FIXED_ORDER_CHARGE == this.priceTotalInPence);
    }

    /**
     * Converts the order to a string in JSON format.
     *
     * @return A String storing the information in JSON format.
     */
    public String toJson() {
        return "{\"orderNo\": \"" + orderNo + "\", "
                + "\"outcome\": \"" + outcome + "\", "
                + "\"costInPence\": " + priceTotalInPence + "}";
    }

    /**
     * Updates the outcome of an order from "ValidButNotDelivered" to "Delivered".
     * Used when the PizzaDrone confirms that the order has been delivered.
     */
    public void setValidOrderToDelivered() {
        if (this.outcome == OrderOutcome.ValidButNotDelivered) {
            this.outcome = OrderOutcome.Delivered;
        } else {
            throw new IllegalStateException(
                    "Order is not valid or has already been delivered, cannot be set to delivered.");
        }
    }

    /**
     * Returns if the order is valid (i.e. the outcome is "ValidButNotDelivered" or
     * "Delivered").
     *
     * @return True if the order is valid, false otherwise.
     */
    public boolean isValid() {
        return this.outcome == OrderOutcome.ValidButNotDelivered || this.outcome == OrderOutcome.Delivered;
    }

    /**
     * Accessor method for the order number.
     *
     * @return The eight-character order number.
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * Accessor method for the order outcome.
     *
     * @return The outcome of the order.
     */
    public Restaurant getRestaurant() {
        return this.restaurantOrderedFrom;
    }

    /**
     * Accessor method for the order date.
     *
     * @return The date of the order.
     */
    public String getDate() {
        return this.orderDate;
    }

    /**
     * Accessor method for the order outcome.
     *
     * @return The outcome of the order.
     */
    public OrderOutcome getOrderOutcome() {
        return this.outcome;
    }

    /**
     * Accessor method for the credit card information.
     *
     * @return The credit card information.
     */
    public CreditCardInformation getCreditCardInformation() {
        return this.creditCardInformation;
    }
}
