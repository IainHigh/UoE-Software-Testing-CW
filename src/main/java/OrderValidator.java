import uk.ac.ed.inf.*;

import java.net.MalformedURLException;
import java.net.URL;

public class OrderValidator {
    private boolean validCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            // Card number must be 16 digits long
            return false;
        }

        // Check that all characters are digits
        return cardNumber.chars().allMatch(Character::isDigit);
    }

    private boolean validCardExpiry(String cardExpiry, String orderDate) {
        // Check that the card expiry is in the format MM/YY
        if (!cardExpiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        // Check that the card expiry is after the order date
        String[] cardExpirySplit = cardExpiry.split("/");
        String[] orderDateSplit = orderDate.split("/");
        int cardExpiryMonth = Integer.parseInt(cardExpirySplit[0]);
        int cardExpiryYear = Integer.parseInt(cardExpirySplit[1]);
        int orderDateMonth = Integer.parseInt(orderDateSplit[0]);
        int orderDateYear = Integer.parseInt(orderDateSplit[1]);
        if (cardExpiryYear < orderDateYear) return false;
        if (cardExpiryYear == orderDateYear && cardExpiryMonth < orderDateMonth) return false;
        return true;
    }

    private boolean validCVV(String cvv) {
        // Check that the CVV is 3 digits long
        if (cvv.length() != 3) {
            return false;
        }

        // Check that all characters are digits
        return cvv.chars().allMatch(Character::isDigit);
    }

    public OrderOutcome validateOrder(Order order) {
        if (!validCardNumber(order.creditCardNumber)) return OrderOutcome.INVALID_CARD_NUMBER;
        if (!validCardExpiry(order.creditCardExpiry, order.orderDate)) return OrderOutcome.INVALID_EXPIRY_DATE;
        if (!validCVV(order.cvv)) return OrderOutcome.INVALID_CVV;
        if (order.orderItems.length == 0 || order.orderItems.length > 4) return OrderOutcome.INVALID_PIZZA_COUNT;
        try {
            int calculatedTotal = order.getDeliveryCost(Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net/restaurants")), order.orderItems);
            if (calculatedTotal != order.priceTotalInPence) return OrderOutcome.INVALID_TOTAL;
        }
        catch (Order.InvalidPizzaCombinationException e) {
            if (e.getMessage() == "All pizzas in an order must be from the same restaurant") {
                return OrderOutcome.INVALID_PIZZA_COMBINATION_MULTIPLE_SUPPLIERS;
            } else if (e.getMessage() == "No pizzas in the order are available from any of the participating restaurants") {
                return OrderOutcome.INVALID_PIZZA_NOT_DEFINED;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return OrderOutcome.VALID_BUT_NOT_DELIVERED;
    }
}
