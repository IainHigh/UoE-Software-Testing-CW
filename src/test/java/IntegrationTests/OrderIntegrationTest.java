package IntegrationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import OrderInformation.CreditCardInformation;
import OrderInformation.Menu;
import OrderInformation.Order;
import OrderInformation.OrderOutcome;
import OrderInformation.OrderRetriever;
import OrderInformation.Pizza;
import OrderInformation.Restaurant;

/*
 * Integration test to validate the OrderInformation package through the entry class "Order.java". This requires the following classes to work:
 * CreditCardInformation.java; Menu.java; Pizza.java; Restaurant.java
 * 
 */

public class OrderIntegrationTest {
    private Restaurant[] restaurants;
    private CreditCardInformation validCardInfo;
    private String validOrderNo;
    private String validOrderDate;
    private int validPriceTotalInPence;
    private Pizza[] validPizzas;

    @Before
    public void setUp() throws Exception {
        // Setup Restaurants and Menu
        Menu[] menu1 = {
                new Menu("Margherita", 800),
                new Menu("Pepperoni", 900)
        };
        Menu[] menu2 = {
                new Menu("Veggie", 850),
                new Menu("BBQ Chicken", 1000)
        };

        restaurants = new Restaurant[] {
                new Restaurant("Pizza Palace", new Restaurant.Location(-3.186874, 55.944494),
                        new String[] { "MONDAY", "TUESDAY" }, menu1),
                new Restaurant("Vegan Delight", new Restaurant.Location(-3.190000, 55.950000),
                        new String[] { "WEDNESDAY", "THURSDAY" }, menu2)
        };

        validOrderNo = "AB12CD34";
        validOrderDate = "2025-01-01";
        validCardInfo = new CreditCardInformation("4111111111111111", "12/30", "123");
        validPriceTotalInPence = 1700 + 100; // Total + Fixed Order Charge
        validPizzas = new Pizza[] {
                new Pizza("Margherita", 800),
                new Pizza("Pepperoni", 900)
        };
    }

    @Test
    public void testValidOrder() {
        Order validOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence, validPizzas);
        validOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be ValidButNotDelivered", OrderOutcome.ValidButNotDelivered,
                validOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidOrderDate() {
        Order invalidOrder = new Order(validOrderNo, "2024-12-31", validCardInfo, validPriceTotalInPence, validPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be Invalid", OrderOutcome.Invalid, invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidPizzaName() {
        Pizza[] invalidPizzas = {
                new Pizza("Nonexistent Pizza", 800),
                new Pizza("Pepperoni", 900)
        };
        Order invalidOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence,
                invalidPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidPizzaNotDefined", OrderOutcome.InvalidPizzaNotDefined,
                invalidOrder.getOrderOutcome());
    }

    @Test
    public void testPizzasFromMultipleRestaurants() {
        Pizza[] mixedPizzas = {
                new Pizza("Margherita", 800),
                new Pizza("BBQ Chicken", 1000)
        };
        Order invalidOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence + 200,
                mixedPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidPizzaCombinationMultipleSuppliers",
                OrderOutcome.InvalidPizzaCombinationMultipleSuppliers, invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidCardNumber() {
        CreditCardInformation invalidCardInfo = new CreditCardInformation("1234567890123456", "12/30", "123"); // Invalid
                                                                                                               // card
                                                                                                               // number
        Order invalidOrder = new Order(validOrderNo, validOrderDate, invalidCardInfo, validPriceTotalInPence,
                validPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidCardNumber", OrderOutcome.InvalidCardNumber,
                invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidCardExpiry() {
        CreditCardInformation expiredCardInfo = new CreditCardInformation("4111111111111111", "01/22", "123"); // Expired
                                                                                                               // card
        Order invalidOrder = new Order(validOrderNo, validOrderDate, expiredCardInfo, validPriceTotalInPence,
                validPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidExpiryDate", OrderOutcome.InvalidExpiryDate,
                invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidCVV() {
        CreditCardInformation invalidCardInfo = new CreditCardInformation("4111111111111111", "12/30", "12"); // Invalid
                                                                                                              // CVV
        Order invalidOrder = new Order(validOrderNo, validOrderDate, invalidCardInfo, validPriceTotalInPence,
                validPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidCvv", OrderOutcome.InvalidCvv, invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidPizzaCount() {
        Pizza[] tooManyPizzas = {
                new Pizza("Margherita", 800),
                new Pizza("Margherita", 800),
                new Pizza("Margherita", 800),
                new Pizza("Margherita", 800),
                new Pizza("Margherita", 800),
        };
        Order invalidOrder = new Order(validOrderNo, validOrderDate, validCardInfo, 4100, tooManyPizzas);
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidPizzaCount", OrderOutcome.InvalidPizzaCount,
                invalidOrder.getOrderOutcome());
    }

    @Test
    public void testInvalidTotalPrice() {
        Order invalidOrder = new Order(validOrderNo, validOrderDate, validCardInfo, 1000, validPizzas); // Incorrect
                                                                                                        // total
        invalidOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be InvalidTotal", OrderOutcome.InvalidTotal, invalidOrder.getOrderOutcome());
    }

    @Test
    public void testSetValidOrderToDelivered() {
        Order validOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence, validPizzas);
        validOrder.validateOrder(restaurants, "2025-01-01");
        assertEquals("Order outcome should be ValidButNotDelivered", OrderOutcome.ValidButNotDelivered,
                validOrder.getOrderOutcome());

        validOrder.setValidOrderToDelivered();
        assertEquals("Order outcome should be Delivered", OrderOutcome.Delivered, validOrder.getOrderOutcome());
    }

    @Test(expected = IllegalStateException.class)
    public void testSetInvalidOrderToDelivered() {
        Order invalidOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence,
                validPizzas);
        invalidOrder.validateOrder(restaurants, "2024-12-31");
        invalidOrder.setValidOrderToDelivered(); // Should throw exception
    }

    @Test
    public void testOrderRetrieverIntegration() throws Exception {
        URL restaurantUrl = new URL("https://ilp-rest-2024.azurewebsites.net/restaurants");
        URL orderUrl = new URL("https://ilp-rest-2024.azurewebsites.net/orders");

        Restaurant[] retrievedRestaurants = OrderRetriever.getRestaurants(restaurantUrl);
        Order[] retrievedOrders = OrderRetriever.getAllOrders(orderUrl);

        assertNotNull("Retrieved restaurants should not be null", retrievedRestaurants);
        assertNotNull("Retrieved orders should not be null", retrievedOrders);

        Order firstOrder = retrievedOrders[0];
        firstOrder.validateOrder(retrievedRestaurants, "2025-01-01");
        assertTrue("First order outcome should be set after validation", firstOrder.getOrderOutcome() != null);
    }

    @Test
    public void testOrderRetrieverGetRestaurants() throws Exception {
        URL restaurantUrl = new URL("https://ilp-rest-2024.azurewebsites.net/restaurants");

        // Simulating restaurant retrieval
        Restaurant[] retrievedRestaurants = OrderRetriever.getRestaurants(restaurantUrl);

        assertNotNull("Retrieved restaurants should not be null", retrievedRestaurants);
        assertTrue("Retrieved restaurants should contain at least one restaurant", retrievedRestaurants.length > 0);

        // Validate a specific restaurant
        Restaurant restaurant = retrievedRestaurants[0];
        assertNotNull("Restaurant should not be null", restaurant);
        assertTrue("Restaurant should have a name", restaurant.getName() != null && !restaurant.getName().isEmpty());
        assertTrue("Restaurant should have a menu", restaurant.getMenu() != null && restaurant.getMenu().length > 0);
    }

    @Test
    public void testOrderRetrieverGetAllOrders() throws Exception {
        URL orderUrl = new URL("https://ilp-rest-2024.azurewebsites.net/orders");

        // Simulating order retrieval
        Order[] retrievedOrders = OrderRetriever.getAllOrders(orderUrl);

        assertNotNull("Retrieved orders should not be null", retrievedOrders);
        assertTrue("Retrieved orders should contain at least one order", retrievedOrders.length > 0);

        // Validate a specific order
        Order order = retrievedOrders[0];
        assertNotNull("Order should not be null", order);
        assertNotNull("Order should have a valid date", order.getDate());
        assertNotNull("Order should have credit card information", order.getCreditCardInformation());
    }

    @Test
    public void testOrderValidationWithRetrieverData() throws Exception {
        URL restaurantUrl = new URL("https://ilp-rest-2024.azurewebsites.net/restaurants");
        URL orderUrl = new URL("https://ilp-rest-2024.azurewebsites.net/orders");

        Restaurant[] retrievedRestaurants = OrderRetriever.getRestaurants(restaurantUrl);
        Order[] retrievedOrders = OrderRetriever.getAllOrders(orderUrl);

        // Validate the first order
        Order firstOrder = retrievedOrders[0];
        firstOrder.validateOrder(retrievedRestaurants, validOrderDate);

        assertNotNull("Order outcome should be set after validation", firstOrder.getOrderOutcome());
    }

    @Test
    public void testRestaurantAccessorsThroughOrder() {
        // Setup a valid order
        Order validOrder = new Order(validOrderNo, validOrderDate, validCardInfo, validPriceTotalInPence, validPizzas);
        validOrder.validateOrder(restaurants, "2025-01-01");

        Restaurant assignedRestaurant = validOrder.getRestaurant();

        assertNotNull("Assigned restaurant should not be null", assignedRestaurant);
        assertEquals("Assigned restaurant name mismatch", "Pizza Palace", assignedRestaurant.getName());
        assertEquals("Assigned restaurant latitude mismatch", 55.944494, assignedRestaurant.getLatitude(), 0.0001);
        assertEquals("Assigned restaurant longitude mismatch", -3.186874, assignedRestaurant.getLongitude(), 0.0001);

        // Test restaurant menu accessors
        Menu[] restaurantMenu = assignedRestaurant.getMenu();
        assertNotNull("Restaurant menu should not be null", restaurantMenu);
        assertEquals("Restaurant menu should have the correct number of items", 2, restaurantMenu.length);
        assertEquals("First menu item name mismatch", "Margherita", restaurantMenu[0].getName());
        assertEquals("First menu item price mismatch", 800, restaurantMenu[0].getPriceInPence());
    }

    @Test
    public void testOrderRetrieverGetOrdersOnDate() throws Exception {
        URL orderUrl = new URL("https://ilp-rest-2024.azurewebsites.net/orders");

        Order[] allOrders = OrderRetriever.getAllOrders(orderUrl);
        Order[] ordersOnDate = OrderRetriever.getOrdersOnDate(allOrders, "2025-01-30");

        assertNotNull("Orders on date should not be null", ordersOnDate);
        assertTrue("Orders on date should not be empty", ordersOnDate.length > 0);

        for (Order order : ordersOnDate) {
            assertEquals("Order date should match the specified date", "2025-01-30", order.getDate());
        }
    }

    @Test
    public void testRestaurantMutators() {
        // Test setting number of moves from Appleton Tower
        Restaurant testRestaurant = restaurants[0];
        testRestaurant.setNumberOfMovesFromAppleton(5);

        assertEquals("Number of moves from Appleton should be set correctly", 5,
                testRestaurant.getNumberOfMovesFromAppletonTower());
    }
}
