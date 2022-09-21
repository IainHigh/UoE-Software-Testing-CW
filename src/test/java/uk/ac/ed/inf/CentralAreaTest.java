package uk.ac.ed.inf;

import static org.junit.Assert.assertTrue;

public class CentralAreaTest {

    public static void testPointInCentralArea(LngLat point, boolean expected) {
        boolean actual = point.inCentralArea();
        System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                "\"coordinates\": [" + point.getLongitude() + ", " + point.getLatitude() + "] }},");
        assertTrue("\n\nExpected value " + expected + " but got " + actual + "\nLongitude: " + point.getLongitude() +
                "\nLatitude: " + point.getLatitude() + "\n\n", actual == expected);
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

    public static void main(String[] args) {
//        LngLat point1 = new LngLat(-3.192473, 55.942637);
//        LngLat point2 = new LngLat(-3.188386951227769, 55.94610360803086);
//
//        testPointInCentralArea(point1, false);
//        testPointInCentralArea(point2, false);

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
