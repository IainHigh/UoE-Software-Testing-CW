package UnitTests.OrderInformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import OrderInformation.Pizza;

public class PizzaUnitTest {
    @Test
    public void testConstructorValidInputs() {
        Pizza pizza = new Pizza("Margherita", 850);
        assertNotNull("Pizza object should be created successfully", pizza);
        assertEquals("Pizza name mismatch", "Margherita", pizza.getName());
        assertEquals("Pizza price mismatch", 850, pizza.getPriceInPence());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyName() {
        new Pizza("", 850);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullName() {
        new Pizza(null, 850);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroPrice() {
        new Pizza("Pepperoni", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativePrice() {
        new Pizza("Hawaiian", -500);
    }

    @Test
    public void testAccessors() {
        Pizza pizza = new Pizza("Funghi", 950);
        assertEquals("getName accessor failed", "Funghi", pizza.getName());
        assertEquals("getPriceInPence accessor failed", 950, pizza.getPriceInPence());
    }

}
