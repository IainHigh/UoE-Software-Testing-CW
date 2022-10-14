package uk.ac.ed.inf.SystemTests;

import uk.ac.ed.inf.PizzaDrone;

import java.util.ArrayList;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        PizzaDrone drone = new PizzaDrone();
        String[] dates = generateAllDates();
        // dates = new String[]{"2023-01-01"};
        for (String date : dates) {
            System.out.println("Testing date: " + date);
            drone.main(new String[]{date, "https://ilp-rest.azurewebsites.net/"});
        }

    }

    private static String[] generateAllDates(){
        List<String> dates = new ArrayList<>();
        for (int i = 1; i <= 1; i++) {
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
