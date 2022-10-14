package uk.ac.ed.inf.SystemTests;

import IO.JSONRetriever;
import org.junit.Test;
import uk.ac.ed.inf.*;

import java.net.MalformedURLException;
import java.net.URL;

public class OrderPatternSystemTest {

    @Test
    public void testPattern() {

        /*
        * Currently the data for orders on the rest API all follow the same pattern.
        * This test is to ensure that the program gets this pattern correct
        * This will probably have to be changed in the future when the rest API is updated
        * */

        Order[] orders;
        try {
            JSONRetriever retriever = new JSONRetriever();
            orders = retriever.getOrders(new URL("https://ilp-rest.azurewebsites.net/orders"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < orders.length; i++){
            OrderValidator validator = new OrderValidator();
            try {
                orders[i].outcome = validator.validateOrder(orders[i], new URL("https://ilp-rest.azurewebsites" +
                        ".net/restaurants"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            System.out.println(i);
            switch (i % 9){
                case 0:
                case 1:
                    assert(orders[i].outcome.equals(OrderOutcome.VALID_BUT_NOT_DELIVERED));
                    break;
                case 2:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_CARD_NUMBER));
                    break;
                case 3:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_EXPIRY_DATE));
                    break;
                case 4:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_CVV));
                    break;
                case 5:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_TOTAL));
                    break;
                case 6:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_PIZZA_NOT_DEFINED));
                    break;
                case 7:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_PIZZA_COUNT));
                    break;
                case 8:
                    assert(orders[i].outcome.equals(OrderOutcome.INVALID_PIZZA_COMBINATION_MULTIPLE_SUPPLIERS) || orders[i].outcome.equals(OrderOutcome.VALID_BUT_NOT_DELIVERED));
                    break;
            }
        }
    }
}
