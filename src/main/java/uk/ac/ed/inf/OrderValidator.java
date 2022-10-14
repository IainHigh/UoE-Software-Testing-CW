package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

public class OrderValidator {

    /**
     * Validates the order and assigns the outcome.
     * @param order The order to validate.
     * @return An OrderOutcome enum value depending on the validity of the order.
     */
    public OrderOutcome validateOrder(Order order, URL restaurantURL) {
        if (!validCardNumber(order.creditCardNumber)) return OrderOutcome.INVALID_CARD_NUMBER;
        if (!validCardExpiry(order.creditCardExpiry, order.orderDate)) return OrderOutcome.INVALID_EXPIRY_DATE;
        if (!validCVV(order.cvv)) return OrderOutcome.INVALID_CVV;
        if (order.orderItems.length == 0 || order.orderItems.length > 4) return OrderOutcome.INVALID_PIZZA_COUNT;
        try {
            // TODO: Should this be in a restaurant singleton?
            int calculatedTotal = order.getDeliveryCost(Restaurant.getRestaurantsFromRestServer(restaurantURL), order.orderItems);
            if (calculatedTotal != order.priceTotalInPence) return OrderOutcome.INVALID_TOTAL;
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
    private boolean validCardExpiry(String cardExpiry, Date orderDate) {
        // TODO: FIX THIS
        // Check that the card expiry is in the format MM/YY
        if (!cardExpiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        // Check that the card expiry is after the order date
        int month = Integer.parseInt(cardExpiry.substring(0, 2));
        int year = Integer.parseInt(cardExpiry.substring(3, 5));

        if (year < orderDate.getYear() - 100) return false;
        if (year == orderDate.getYear() - 100 && month < orderDate.getMonth() + 1) return false;
        return true;
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
}
