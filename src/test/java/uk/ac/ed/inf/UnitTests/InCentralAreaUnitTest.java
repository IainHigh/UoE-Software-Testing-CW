package uk.ac.ed.inf.UnitTests;

import RouteCalculation.AreaSingleton;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ed.inf.Constants;
import RouteCalculation.LngLat;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class InCentralAreaUnitTest {

    @Before
    public void setUp() throws MalformedURLException {
        String restAPIUrl = "https://ilp-rest.azurewebsites.net";
        AreaSingleton.getInstance().setURLs(
                new URL(restAPIUrl + Constants.CENTRAL_AREA_URL_SLUG),
                new URL(restAPIUrl + Constants.NO_FLY_ZONES_URL_SLUG));
    }

    @Test
    public void testInvalidLines(){
        // Top edge
        testPointInCentralArea(new LngLat(-4.188386951227769, 55.946233), false);

        // Bottom edge
        testPointInCentralArea(new LngLat(-4.188386951227769, 55.942617), false);

        // Left edge
        testPointInCentralArea(new LngLat(-3.192473, 56.944425), false);

        // Right edge
        testPointInCentralArea(new LngLat(-3.184319, 56.944425), false);
    }

    @Test
    public void testValidPoints(){
        for (int i = 0; i < 100; i++){
            testPointInCentralArea(generateValidPoint(), true);
        }
    }

    @Test
    public void testInvalidPoints(){
        for (int i = 0; i < 100; i++){
            testPointInCentralArea(generateInvalidPoint(), false);
        }
    }

    private void testPointInCentralArea(LngLat point, boolean expected) {
        boolean actual = point.inCentralArea();
        assertEquals("\n\nExpected value " + expected + " but got " + actual + "\nLongitude: " + point.lng() +
                "\nLatitude: " + point.lat() + "\n\n", expected, actual);
    }

    private LngLat generateValidPoint(){
        double[] point = new double[2];
        point[0] = Math.random() * (-3.184319 + 3.192473) - 3.192473;
        point[1] = Math.random() * (55.946233 - 55.942617) + 55.942617;
        return new LngLat(point[0], point[1]);
    }

    private LngLat generateInvalidPoint(){
        double[] point = new double[2];
        point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
        point[1] = Math.random() * (55.947233 - 55.941617) + 55.941617;
        while (point[0] > -3.192473 && point[0] < -3.184319 && point[1] > 55.942617 && point[1] < 55.946233){
            point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
            point[1] = Math.random() * (55.946233 - 55.942617) + 55.941617;
        }
        return new LngLat(point[0], point[1]);
    }
}
