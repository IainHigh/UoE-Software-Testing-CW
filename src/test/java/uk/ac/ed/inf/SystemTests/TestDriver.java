package uk.ac.ed.inf.SystemTests;

import uk.ac.ed.inf.PizzaDrone;

import java.util.ArrayList;
import java.util.List;

import java.time.Instant;
import java.time.Duration;

public class TestDriver {
    public static void main(String[] args) {

        String[] dates = generateAllDates();
        for (String date : dates) {
            System.out.println("Testing date: " + date);
            Instant start = Instant.now();
            PizzaDrone.main(new String[]{date, "https://ilp-rest.azurewebsites.net/"});
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
