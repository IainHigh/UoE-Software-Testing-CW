package UnitTests.OrderInformation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import OrderInformation.Menu;
import OrderInformation.Restaurant;

public class RestaurantUnitTest {
    @Test
    public void testConstructorValidInputs() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        Restaurant restaurant = new Restaurant("Pizzaiolo", location, validOpeningDays, menu);
        assertNotNull("Restaurant object should be created successfully", restaurant);
        assertEquals("Restaurant name mismatch", "Pizzaiolo", restaurant.getName());
        assertArrayEquals("Opening days mismatch", validOpeningDays, restaurant.getOpeningDays());
        assertEquals("Menu size mismatch", 1, restaurant.getMenu().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidOpeningDaysDuplicates() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] invalidOpeningDays = { "MONDAY", "MONDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        new Restaurant("Pizzaiolo", location, invalidOpeningDays, menu);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidOpeningDaysInvalidDay() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] invalidOpeningDays = { "MONDAY", "INVALIDDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        new Restaurant("Pizzaiolo", location, invalidOpeningDays, menu);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyName() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        new Restaurant("", location, validOpeningDays, menu);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullLocation() {
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        new Restaurant("Pizzaiolo", null, validOpeningDays, menu);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullMenu() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };

        new Restaurant("Pizzaiolo", location, validOpeningDays, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLongitude() {
        new Restaurant.Location(-200, 55.944494);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLatitude() {
        new Restaurant.Location(-3.186874, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLongitudeLatitude() {
        new Restaurant.Location(-200, 100);
    }

    @Test
    public void testGetLatitudeLongitude() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        Restaurant restaurant = new Restaurant("Pizzaiolo", location, validOpeningDays, menu);
        assertEquals("Latitude mismatch", 55.944494, restaurant.getLatitude(), 0.0001);
        assertEquals("Longitude mismatch", -3.186874, restaurant.getLongitude(), 0.0001);
    }

    @Test
    public void testSetNumberOfMovesFromAppleton() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        Restaurant restaurant = new Restaurant("Pizzaiolo", location, validOpeningDays, menu);
        restaurant.setNumberOfMovesFromAppleton(5);
        assertEquals("Number of moves mismatch", 5, restaurant.getNumberOfMovesFromAppletonTower());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfMovesFromAppletonInvalid() {
        Restaurant.Location location = new Restaurant.Location(-3.186874, 55.944494);
        String[] validOpeningDays = { "MONDAY", "TUESDAY", "FRIDAY" };
        Menu[] menu = { new Menu("Pizza Margherita", 850) };

        Restaurant restaurant = new Restaurant("Pizzaiolo", location, validOpeningDays, menu);
        restaurant.setNumberOfMovesFromAppleton(-5);
    }
}
