package OrderInformation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent credit card information in an order.
 */
public class CreditCardInformation {
    @JsonProperty("creditCardNumber")
    private String creditCardNumber;

    @JsonProperty("creditCardExpiry")
    private String creditCardExpiry;

    @JsonProperty("cvv")
    private String cvv;

    // Getters
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public String getCvv() {
        return cvv;
    }
}
