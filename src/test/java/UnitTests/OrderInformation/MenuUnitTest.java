package UnitTests.OrderInformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import OrderInformation.Menu;

public class MenuUnitTest {

    @Test
    public void testConstructorValidInputs() {
        Menu menuItem = new Menu("Pizza Margherita", 850);
        assertNotNull("Menu item should be created successfully", menuItem);
        assertEquals("Menu item name mismatch", "Pizza Margherita", menuItem.getName());
        assertEquals("Menu item price mismatch", 850, menuItem.getPriceInPence());
    }

    @Test
    public void testAccessors() {
        Menu menuItem = new Menu("Pizza Funghi", 950);
        assertEquals("getName accessor failed", "Pizza Funghi", menuItem.getName());
        assertEquals("getPriceInPence accessor failed", 950, menuItem.getPriceInPence());
    }

    // Test the constructor with empty string name.
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyName() {
        new Menu("", 500);
    }

    // Test the constructor with null name.
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullName() {
        new Menu(null, 700);
    }

    // Test the constructor with zero price.
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroPrice() {
        new Menu("Pizza Marinara", 0);
    }

    // Test the constructor with negative price.
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativePrice() {
        new Menu("Pizza Pepperoni", -200);
    }

}
