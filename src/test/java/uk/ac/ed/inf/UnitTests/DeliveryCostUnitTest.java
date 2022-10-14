package uk.ac.ed.inf.UnitTests;

import org.junit.Test;
import uk.ac.ed.inf.JSONRetriever;
import uk.ac.ed.inf.Order;
import uk.ac.ed.inf.Restaurant;

import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.*;

public class DeliveryCostUnitTest {
    @Test
    public void testInvalidOrders() {
        Restaurant[] participants = getRestaurantsFromRestServer();

        // Order consists of pizzas from two separate restaurants.
        String[] invalidOrder = {"Margarita", "Calzone", "Meat Lover"};
        testInvalidDeliveryCost(participants, invalidOrder, "Pizzas cannot be ordered from different restaurants");

        // Similar to the first test.
        String[] invalidOrder3 = {"Margarita", "Vegan Delight"};
        testInvalidDeliveryCost(participants, invalidOrder3, "Pizzas cannot be ordered from different restaurants");

        // The order contains a pizza that is not severed by any of the restaurants.
        String[] invalidOrder2 = {"M"};
        testInvalidDeliveryCost(participants, invalidOrder2, "Invalid pizza ordered");

        // Test having valid pizzas and invalid pizzas in the same order.
        String[] invalidOrder4 = {"Margarita", "M"};
        testInvalidDeliveryCost(participants, invalidOrder4, "Invalid pizza ordered");

        // The empty order should not be valid.
        String[] invalidOrder5 = {};
        testInvalidDeliveryCost(participants, invalidOrder5, "Invalid input");

        // The null order should not be valid.
        testInvalidDeliveryCost(participants, null, "Invalid input");

        // The null order should not be valid.
        String[] validOrder = {"Margarita"};
        testInvalidDeliveryCost(null, validOrder, "Invalid input");
    }

    @Test
    public void testValidOrders() {
        Restaurant[] participants = getRestaurantsFromRestServer();

        String[] order1 = {"Margarita", "Calzone"};
        testValidDeliveryCost(participants, order1, 1000 + 1400 + 100);

        String[] order2 = {"Meat Lover", "Vegan Delight"};
        testValidDeliveryCost(participants, order2, 1400 + 1100 + 100);

        String[] order3 = {"Super Cheese", "All Shrooms"};
        testValidDeliveryCost(participants, order3, 1400 + 900 + 100);

        String[] order4 = {"Proper Pizza", "Pineapple & Ham & Cheese"};
        testValidDeliveryCost(participants, order4, 1400 + 900 + 100);

        String[] order5 = {"Margarita", "Margarita", "Margarita"};
        testValidDeliveryCost(participants, order5, 1000 + 1000 + 1000 + 100);
    }

    @Test
    public void testRESTOrders() {
        Restaurant[] participants;
        Order[] orders;

        try {
            orders = getOrdersFromRestServer();
            participants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net/restaurants"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        int numberOfMatchingPrices = 0;
        int numberOfValidOrders = 0;
        for (Order order : orders) {
            try {
                if (order.priceTotalInPence == order.getDeliveryCost(participants, order.orderItems)) {
                    numberOfMatchingPrices++;
                }
                numberOfValidOrders++;
            } catch (Order.InvalidPizzaCombinationException ignored) {}
        }
        assertTrue("We should expect more than 75% of the orders to have matching prices",
                numberOfMatchingPrices > numberOfValidOrders * 0.75);
    }

    private Order[] getOrdersFromRestServer() {
        try {
            JSONRetriever retriever = new JSONRetriever();
            return retriever.getOrders(new URL("https://ilp-rest.azurewebsites.net/orders"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private Restaurant[] getRestaurantsFromRestServer() {
        try {
            return Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites" +
                    ".net/restaurants"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void testValidDeliveryCost(Restaurant[] participants, String[] order, int expectedPrice){
        Order o = new Order();
        int actualPrice;
        try{
            actualPrice = o.getDeliveryCost(participants, order);
        } catch (Order.InvalidPizzaCombinationException e) {
            actualPrice = -1;
        }
        assertEquals("Actual price different from expected price", expectedPrice, actualPrice);
    }

    private void testInvalidDeliveryCost(Restaurant[] participants, String[] order, String expectedErrorMessage){
        Order o = new Order();
        try{
            o.getDeliveryCost(participants, order);
            fail("Expected an InvalidPizzaCombinationException to be thrown");
        } catch (Order.InvalidPizzaCombinationException e) {
            assertEquals("Actual error message different from expected error message", expectedErrorMessage, e.getMessage());
        }
    }
}
