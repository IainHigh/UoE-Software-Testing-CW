package SystemTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import PizzaDronz.PizzaDrone;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

@RunWith(Parameterized.class)
public class SystemTest {

    private String date;

    public SystemTest(String date) {
        this.date = date;
    }

    @Parameters
    public static Collection<String> dates() {
        return generateAllDates();
    }

    @Test
    public void testDate() {
        // Suggest garbage collection
        System.gc();
        
        System.out.println("Testing date: " + date);
        Instant start = Instant.now();

        try {
            PizzaDrone.main(new String[] { date, "https://ilp-rest-2024.azurewebsites.net/" });
        } catch (Exception e) {
            System.err.println("An exception was thrown: " + e.getMessage());
            assert (false);
        }

        // Assert the time taken in seconds is less than 60
        assert (Duration.between(start, Instant.now()).getSeconds() < 60) : "Exception: Time taken exceeded 60 seconds";
    }

    private static List<String> generateAllDates() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now(); // Get current date
        LocalDate endDate = today.plusMonths(1); // Calculate end date

        // Add all dates from today to endDate (inclusive)
        while (!today.isAfter(endDate)) {
            dates.add(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            today = today.plusDays(1); // Increment by 1 day
        }
        return dates;
    }
}