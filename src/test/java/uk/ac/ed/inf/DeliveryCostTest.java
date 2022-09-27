package uk.ac.ed.inf;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class DeliveryCostTest {

    public static Order[] getOrdersFromRestServer() {
        try {
            JSONRetriever retriever = new JSONRetriever();
            return retriever.getOrders(new URL("https://ilp-rest.azurewebsites.net/orders"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testValidDeliveryCost(Restaurant[] participants, String[] order, int expectedPrice){

        Order o = new Order();
        int actualPrice;
        try{
            actualPrice = o.getDeliveryCost(participants, order);
        } catch (Order.InvalidPizzaCombinationException e) {
            actualPrice = 0;
        }

        if (actualPrice == expectedPrice) {
            System.out.println("Test passed");
        }
        // assertEquals("Actual price different from expected price", actualPrice, expectedPrice);
    }

    public static void testInvalidDeliveryCost(Restaurant[] participants, String[] order){
        assertThrows(Order.InvalidPizzaCombinationException.class, () -> {
            Order o = new Order();
            o.getDeliveryCost(participants, order);
        });
    }

    @Test
    public void main() {
        try {
            Order[] orders = getOrdersFromRestServer();
            Restaurant[] participants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net/restaurants"));
            for (Order order : orders) {
                testValidDeliveryCost(participants, order.orderItems, order.priceTotalInPence - 900);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


        try {
            Restaurant[] participants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites" +
                    ".net/restaurants"));
            String[] order1 = {"Margarita", "Calzone"};
            String[] order2 = {"Meat Lover", "Vegan Delight"};
            String[] order3 = {"Super Cheese", "All Shrooms"};
            String[] order4 = {"Proper Pizza", "Pineapple & Ham & Cheese"};
            testValidDeliveryCost(participants, order1, 1000 + 1400 + 100);
            testValidDeliveryCost(participants, order2, 1400 + 1100 + 100);
            testValidDeliveryCost(participants, order3, 1400 + 900 + 100);
            testValidDeliveryCost(participants, order4, 1400 + 900 + 100);


            String[] invalidOrder = {"Margarita", "Calzone", "Meat Lover"};
            testInvalidDeliveryCost(participants, invalidOrder);

            String[] invalidOrder2 = {"M"};
            testInvalidDeliveryCost(participants, invalidOrder2);

            String[] invalidOrder3 = {"Margarita", "Vegan Delight"};
            testInvalidDeliveryCost(participants, invalidOrder3);



        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
}
