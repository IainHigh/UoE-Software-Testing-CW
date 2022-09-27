package uk.ac.ed.inf;

import org.junit.Test;
import java.util.Random;

public class NextPositionTest {
    public static void testNextPosition(LngLat point){
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.N);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SSE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.ESE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.E);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.ENE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NNE);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.N);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NNW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.NW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.WNW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.W);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.WSW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.SSW);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
        for (int i = 0; i < 100; i++){
            point = point.nextPosition(CompassDirection.S);
            System.out.println("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                    "\"coordinates\": [" + point.lng + ", " + point.lat + "] }},");
        }
    }

    public static void testHover(){
        // Generate random lat and long using pseudorandom number generator
        Random rand = new Random();
        for (int i = 0; i < 100; i++){
            double lat = rand.nextDouble() * 180 - 90;
            double lng = rand.nextDouble() * 360 - 180;
            LngLat point = new LngLat(lng, lat);
            LngLat hoverPoint = point.nextPosition(CompassDirection.HOVER);
            assert(hoverPoint.lat == point.lat);
        }
    }
    @Test
    public void main() {
        LngLat point = new LngLat(0, 0);
        testNextPosition(point);
        testHover();
    }
}
