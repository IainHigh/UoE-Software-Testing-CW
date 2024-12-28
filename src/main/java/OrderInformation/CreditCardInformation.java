package OrderInformation;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public CreditCardInformation(
        @JsonProperty("creditCardNumber") String creditCardNumber,
        @JsonProperty("creditCardExpiry") String creditCardExpiry,
        @JsonProperty("cvv") String cvv
    ) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
    }

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

        /**
     * Validates the credit card number.
     * The regex used is provided by stackoverflow users "quinlo" and "ajithparamban".
     * <a href="https://stackoverflow.com/questions/9315647/regex-credit-card-number-tests">...</a>
     *
     * @return True if the credit card number is valid. False otherwise.
     */
    public boolean validCardNumber() {
        if (this.creditCardNumber == null) return false;
        final String VISA_REGEX = "^4[0-9]{12}(?:[0-9]{3})?$";
        final String MASTER_CARD_REGEX = "^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1" +
                "][0-9]{13}|720[0-9]{12}))$";

        if (this.getCreditCardNumber().matches(VISA_REGEX) || 
            this.getCreditCardNumber().matches(MASTER_CARD_REGEX)) {
            // Check if the credit card number is a valid visa or mastercard number.
            return luhnCheck();
        }
        return false;
    }

    /**
     * Uses the luhn algorithm to check if the card number is valid.
     * <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">...</a>
     *
     * @return True if the credit card number passes the luhn check. False otherwise.
     */
    public boolean luhnCheck() {
        int sum = 0;
        String cardNumber = this.getCreditCardNumber();
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (i % 2 == 0) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
        }
        return (sum % 10 == 0);
    }

    /**
     * Validates the CVV of a credit card.
     *
     * @return True if the CVV is valid (3 digits, all numbers), false otherwise.
     */
    public boolean validCVV() {
        String cvv = this.getCvv();
        if (cvv.length() != 3) return false;
        return cvv.chars().allMatch(Character::isDigit);
    }

    /**
     * Validates the expiration date of a credit card.
     *
     * @return True if the expiration date is is a valid date, false otherwise.
     */
    public boolean validExpiryDate() {
        if (this.creditCardExpiry == null || this.creditCardExpiry.length() != 5) {
            return false;
        }
        try {
            YearMonth.parse(this.creditCardExpiry, DateTimeFormatter.ofPattern("MM/yy"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
