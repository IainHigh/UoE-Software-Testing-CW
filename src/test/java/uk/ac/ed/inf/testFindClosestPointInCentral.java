package uk.ac.ed.inf;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class testFindClosestPointInCentral {

    public static LngLat generatePoint(){
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
    public void main(){
        String restAPIUrl = "https://ilp-rest.azurewebsites.net/";
        try {
            FlyZoneSingleton.getInstance().setURLs(new URL(restAPIUrl + "/centralarea"), new URL(restAPIUrl + "/noflyzones"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 100; i++) {
            LngLat point1 = generatePoint();
            LngLat point2 = RouteCalculator.findClosestPointInCentralArea(point1);

            assertTrue(point2.inCentralArea());
            for (CompassDirection direction : CompassDirection.values()) {
                LngLat point3 = point2.nextPosition(direction);
                if (point3.inCentralArea()) {
                    // Print point 1, 2, and 3 in geojson format
                    System.out.println("{" +
                            "\"type\": \"Feature\"," +
                            "\"geometry\": {" +
                            "\"type\": \"LineString\"," +
                            "\"coordinates\": [" +
                            "[" + point1.lng() + ", " + point1.lat() + "]," +
                            "[" + point2.lng() + ", " + point2.lat() + "]," +
                            "[" + point3.lng() + ", " + point3.lat() + "]" +
                            "]" +
                            "}" +
                            "}");
                    System.out.println(point3.distanceTo(point1));
                    System.out.println(point2.distanceTo(point1));
                    assertTrue(point3.distanceTo(point1) >= point2.distanceTo(point1));
                }
            }
        }
    }
}
