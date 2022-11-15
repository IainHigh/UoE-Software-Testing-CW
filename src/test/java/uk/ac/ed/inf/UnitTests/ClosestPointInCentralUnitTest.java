package uk.ac.ed.inf.UnitTests;

import IO.RestAPIDataSingleton;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ed.inf.CompassDirection;
import uk.ac.ed.inf.LngLat;
import uk.ac.ed.inf.RouteCalculator;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class ClosestPointInCentralUnitTest {

    @Before
    public void setUp() throws MalformedURLException {
        String base = "https://ilp-rest.azurewebsites.net";
        RestAPIDataSingleton.getInstance().setURLs(
                new URL( base + "/centralarea"),
                new URL(base + "/noflyzones"),
                new URL(base + "/restaurants"),
                new URL(base + "/orders/"));
    }

    private static LngLat generatePoint(){
        double[] point = new double[2];
        point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
        point[1] = Math.random() * (55.947233 - 55.941617) + 55.941617;
        while (point[0] > -3.192473 && point[0] < -3.184319 && point[1] > 55.942617 && point[1] < 55.946233){
            point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
            point[1] = Math.random() * (55.946233 - 55.942617) + 55.941617;
        }
        return new LngLat(point[0], point[1]);
    }

    @Test
    public void TestClosestPointInCentral(){
        for (int i = 0; i < 10000; i++) {
            LngLat point1 = generatePoint();
            LngLat point2 = RouteCalculator.findClosestPointInCentralArea(point1);

            // The point should be in the central area
            assertTrue(point2.inCentralArea());
            for (CompassDirection direction : CompassDirection.values()) {
                LngLat point3 = point2.nextPosition(direction);
                if (point3.inCentralArea()) {
                    // It should be closer than any other point in the central area.
                    assertTrue(point3.distanceTo(point1) >= point2.distanceTo(point1));
                }
            }
        }
    }
}
