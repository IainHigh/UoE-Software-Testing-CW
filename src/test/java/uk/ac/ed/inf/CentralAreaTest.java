package uk.ac.ed.inf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CentralAreaTest {

    public static void testPointInCentralArea(LngLat point, boolean expected) {
        boolean actual = point.inCentralArea();
        System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        assertEquals("\n\nExpected value " + expected + " but got " + actual + "\nLongitude: " + point.lng +
                "\nLatitude: " + point.lat + "\n\n", actual, expected);
    }

    public static LngLat generateValidPoint(){
        double[] point = new double[2];
        point[0] = Math.random() * (-3.184319 + 3.192473) - 3.192473;
        point[1] = Math.random() * (55.946233 - 55.942617) + 55.942617;

        return new LngLat(point[0], point[1]);
    }

    public static LngLat generateInvalidPoint(){
        double[] point = new double[2];
        point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
        point[1] = Math.random() * (55.947233 - 55.941617) + 55.941617;
        while (point[0] > -3.192473 && point[0] < -3.184319 && point[1] > 55.942617 && point[1] < 55.946233){
            point[0] = Math.random() * (-3.179319 + 3.197473) - 3.197473;
            point[1] = Math.random() * (55.946233 - 55.942617) + 55.941617;
        }
        return new LngLat(point[0], point[1]);
    }

    public static void testEdgesAndCorners(){
        // Test top left corner
        LngLat point2 = new LngLat(-3.192473, 55.946233);
        testPointInCentralArea(point2, true);

        // Test bottom left corner
        LngLat point3 = new LngLat(-3.192473, 55.942617);
        testPointInCentralArea(point3, true);

        // Test bottom right corner
        LngLat point4 = new LngLat(-3.184319, 55.942617);
        testPointInCentralArea(point4, true);

        // Test top right corner
        LngLat point5 = new LngLat(-3.184319, 55.946233);
        testPointInCentralArea(point5, true);

        // Top edge
        LngLat point6 = new LngLat(-3.188386951227769, 55.946233);
        testPointInCentralArea(point6, true);

        // Bottom edge
        LngLat point7 = new LngLat(-3.188386951227769, 55.942617);
        testPointInCentralArea(point7, true);

        // Left edge
        LngLat point8 = new LngLat(-3.192473, 55.944425);
        testPointInCentralArea(point8, true);

        // Right edge
        LngLat point9 = new LngLat(-3.184319, 55.944425);
        testPointInCentralArea(point9, true);
    }

    @Test
    public void main() {

        testEdgesAndCorners();

        for (int i = 0; i < 1000; i++){
            LngLat point = generateValidPoint();
            testPointInCentralArea(point, true);
        }
        for (int i = 0; i < 1000; i++){
            LngLat point = generateInvalidPoint();
            testPointInCentralArea(point, false);
        }
    }
}
