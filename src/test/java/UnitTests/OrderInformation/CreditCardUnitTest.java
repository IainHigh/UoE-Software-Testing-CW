package UnitTests.OrderInformation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import OrderInformation.CreditCardInformation;

public class CreditCardUnitTest {

    // Credit Card Number Tests
    @Test
    public void creditCard_validCardNumber_rejectsNumberTooShort() {
        String cardNumber = "1234567";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "123");
        assertFalse(creditCard.validCardNumber());
    }

    @Test
    public void creditCard_validCardNumber_rejectsNumberTooLong() {
        String cardNumber = "1234567891234567891";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "123");
        assertFalse(creditCard.validCardNumber());
    }

    @Test
    public void creditCard_validCardNumber_rejectsNumberWithNonNumericCharacters() {
        String cardNumber = "123456789012345a";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "123");
        assertFalse(creditCard.validCardNumber());
    }

    @Test
    public void creditCard_validCardNumber_rejectsFailedLuhn() {
        String cardNumber = "1234567890123456";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "123");
        assertFalse(creditCard.validCardNumber());
    }

    @Test
    public void creditCard_validCardNumber_rejectsNull() {
        CreditCardInformation creditCard = new CreditCardInformation(null, "12/25", "123");
        assertFalse(creditCard.validCardNumber());
    }

    @Test
    public void creditCard_validCardNumber_acceptsValidNumber() {
        String cardNumber = "4242424242424242";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "123");
        assertTrue(creditCard.validCardNumber());
        assertEquals(cardNumber, creditCard.getCreditCardNumber());
    }

    // Expiry Date Tests
    @Test
    public void creditCard_validExpiryDate_rejectsDateTooShort() {
        String expiryDate = "12/2";
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", expiryDate, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_rejectsDateTooLong() {
        String expiryDate = "12/2025";
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", expiryDate, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_rejectsNonNumericCharacters() {
        String expiryDate = "12/2a";
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", expiryDate, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_rejectsMonthTooLarge() {
        String expiryDate = "13/25";
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", expiryDate, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_rejectsMonthTooSmall() {
        String expiryDate = "00/25";
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", expiryDate, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_rejectsNullDate() {
        CreditCardInformation creditCard = new CreditCardInformation("4242424242424242", null, "123");
        assertFalse(creditCard.validExpiryDate());
    }

    @Test
    public void creditCard_validExpiryDate_acceptsValidDate() {
        String cardNumber = "4242424242424242";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "06/25", "123");
        assertTrue(creditCard.validExpiryDate());
        assertEquals("06/25", creditCard.getCreditCardExpiry());
    }

    // CVV Tests

    @Test
    public void creditCard_validCVV_rejectsCVVTooLong() {
        String cardNumber = "4242424242424242";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "1234");
        assertFalse(creditCard.validCVV());
    }

    @Test
    public void creditCard_validCVV_rejectsLetters() {
        String cardNumber = "4242424242424242";
        CreditCardInformation creditCard = new CreditCardInformation(cardNumber, "12/25", "12A");
        assertFalse(creditCard.validCVV());
    }
}
