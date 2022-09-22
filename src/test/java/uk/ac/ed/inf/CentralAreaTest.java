package uk.ac.ed.inf;

import static org.junit.Assert.assertTrue;

public class CentralAreaTest {

    public static void testPointInCentralArea(LngLat point, boolean expected) {
        boolean actual = point.inCentralArea();
        System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        assertTrue("\n\nExpected value " + expected + " but got " + actual + "\nLongitude: " + point.longitude +
                "\nLatitude: " + point.latitude + "\n\n", actual == expected);
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

    public static void testNextPosition(LngLat point){
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.N);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SSE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.ESE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.E);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.ENE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NNE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.N);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NNW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.WNW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.W);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.WSW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SSW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.S);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.longitude + ", " + point.latitude + "] }},");
        }
    }

    public static void main(String[] args) {
//        LngLat point1 = new LngLat(-3.192473, 55.942637);
//        LngLat point2 = new LngLat(-3.188386951227769, 55.94610360803086);
//
//        testPointInCentralArea(point1, false);
//        testPointInCentralArea(point2, false);

        LngLat point1 = new LngLat(0, 0);
        testNextPosition(point1);

        /*
        for (int i = 0; i < 1000; i++){
            LngLat point = generateValidPoint();
            testPointInCentralArea(point, true);
        }
        for (int i = 0; i < 1000; i++){
            LngLat point = generateInvalidPoint();
            testPointInCentralArea(point, false);
        }

         */
    }
}
