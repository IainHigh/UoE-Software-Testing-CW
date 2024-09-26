package uk.ac.ed.inf.SystemTests;

import uk.ac.ed.inf.PizzaDrone;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.Instant;
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
        System.out.println("Testing date: " + date);
        Instant start = Instant.now();

        try{
            PizzaDrone.main(new String[]{date, "https://ilp-rest.azurewebsites.net/"});
        }
        catch (Exception e){
            System.out.println("An exception was thrown: " + e.getMessage());
            assert(false);
        }

        // Assert the time taken in seconds is less than 60
        assert(Duration.between(start, Instant.now()).getSeconds() < 60);
    }

    private static List<String> generateAllDates(){
        List<String> dates = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 31; j++) {
                if ((i == 2 && j > 28) || (i == 4 && j > 30)) {
                    break;
                }
                dates.add("2023-" + String.format("%02d", i) + "-" +String.format("%02d", j));
            }
        }
        return dates;
    }
}