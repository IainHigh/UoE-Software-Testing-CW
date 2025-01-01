package SystemTests;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import PizzaDronz.PizzaDrone;

public class InvalidParameterSystemTest {

    @Test
    // Rejects invalid date
    public void app_main_rejectsInvalidDate() {
        assertThrows("Invalid input. Unable to continue.", IllegalArgumentException.class,
                () -> PizzaDrone.main(new String[] { "ThisAintADateLol", "https://ilp-rest-2024.azurewebsites.net/" }));
    }

    @Test
    // Rejects invalid URL
    public void app_main_rejectsInvalidUrl() {
        assertThrows("Invalid input. Unable to continue.", IllegalArgumentException.class,
                () -> PizzaDrone.main(new String[] { "2023-05-16", "InvalidURL" }));
    }

    @Test
    // Rejects no arguments
    public void app_main_rejectsNoArguments() {
        assertThrows("Invalid input. Unable to continue.", IllegalArgumentException.class,
                () -> PizzaDrone.main(new String[] {}));
    }
}
