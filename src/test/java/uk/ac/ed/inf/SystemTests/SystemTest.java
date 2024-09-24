package uk.ac.ed.inf.SystemTests;

import uk.ac.ed.inf.PizzaDrone;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import java.time.Instant;

import java.time.Duration;


public class SystemTest {

    @Test
    public void testAllDates() {
        // This test runs the program with all possible dates. If an exception is thrown, there is a bug.

        String[] dates = generateAllDates();
        for (String date : dates) {
            System.out.println("Testing date: " + date);
            Instant start = Instant.now();

            try{
                PizzaDrone.main(new String[]{date, "https://ilp-rest.azurewebsites.net/"});
                // Try running the program with the given date. If an exception is thrown, there is a bug.
            }
            catch (Exception e){
                System.out.println("An exception was thrown: " + e.getMessage());
                assert(false);
            }

            System.out.println("\tTime taken: " + Duration.between(start, Instant.now()).toMillis() + "ms");
            
        }
    }

    private static String[] generateAllDates(){
        List<String> dates = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 31; j++) {
                if ((i == 2 && j > 28) || (i == 4 && j > 30)) {
                    break;
                }
                dates.add("2023-" + String.format("%02d", i) + "-" +String.format("%02d", j));
            }
        }
        return dates.toArray(new String[0]);
    }
}
