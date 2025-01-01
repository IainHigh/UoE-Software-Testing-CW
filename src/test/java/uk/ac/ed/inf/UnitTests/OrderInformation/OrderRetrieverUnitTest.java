package uk.ac.ed.inf.UnitTests.OrderInformation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.MalformedURLException;

import org.junit.Test;

import OrderInformation.Order;
import OrderInformation.OrderRetriever;
import OrderInformation.Restaurant;

import java.net.URL;

public class OrderRetrieverUnitTest {
    
    @Test
    public void testGetRestaurantsValidURL() throws MalformedURLException {
        URL url = new URL("https://ilp-rest-2024.azurewebsites.net/restaurants");
        Restaurant[] restaurants = OrderRetriever.getRestaurants(url);
        assertNotNull("Restaurants should not be null", restaurants);
        assertTrue("Restaurants array should not be empty", restaurants.length > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testGetRestaurantsInvalidURL() throws MalformedURLException {
        URL invalidUrl = new URL("https://invalid-url.com/restaurants");
        OrderRetriever.getRestaurants(invalidUrl);
    }

    @Test
    public void testGetAllOrdersValidURL() throws MalformedURLException {
        URL url = new URL("https://ilp-rest-2024.azurewebsites.net/orders");
        Order[] orders = OrderRetriever.getAllOrders(url);
        assertNotNull("Orders should not be null", orders);
        assertTrue("Orders array should not be empty", orders.length > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllOrdersInvalidURL() throws MalformedURLException {
        URL invalidUrl = new URL("https://invalid-url.com/orders");
        OrderRetriever.getAllOrders(invalidUrl);
    }
}
