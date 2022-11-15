package uk.ac.ed.inf.UnitTests;

import org.junit.Before;
import org.junit.Test;
import IO.RestAPIDataSingleton;
import uk.ac.ed.inf.LngLat;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class InCentralAreaUnitTest {

    @Before
    public void setUp() throws MalformedURLException {
        String base = "https://ilp-rest.azurewebsites.net";
        RestAPIDataSingleton.getInstance().setURLs(
                new URL( base + "/centralarea"),
                new URL(base + "/noflyzones"),
                new URL(base + "/restaurants"),
                new URL(base + "/orders/"));
    }

    @Test
    public void testEdgesAndCorners(){
        // Top left corner
        testPointInCentralArea(new LngLat(-3.192473, 55.946233), true);

        // Bottom left corner
        testPointInCentralArea(new LngLat(-3.192473, 55.942617), true);

        // Bottom right corner
        testPointInCentralArea(new LngLat(-3.184319, 55.942617), true);

        // Top right corner
        testPointInCentralArea(new LngLat(-3.184319, 55.946233), true);

        // Top edge
        testPointInCentralArea(new LngLat(-3.188386951227769, 55.946233), true);

        // Bottom edge
        testPointInCentralArea(new LngLat(-3.188386951227769, 55.942617), true);

        // Left edge
        testPointInCentralArea(new LngLat(-3.192473, 55.944425), true);

        // Right edge
        testPointInCentralArea(new LngLat(-3.184319, 55.944425), true);
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
