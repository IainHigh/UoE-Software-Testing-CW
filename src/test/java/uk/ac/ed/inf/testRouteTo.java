package uk.ac.ed.inf;

import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class testRouteTo {

    public LngLat generateSecondPoint(LngLat p1) {
        // Generates a point that is within a radius of 0.0015 degrees of the first point
        Random random = new Random();
        double radius = 0.05 * random.nextDouble();
        double angle = random.nextDouble() * 2 * Math.PI;
        double lng2 = p1.lng() + radius * Math.cos(angle);
        double lat2 = p1.lat() + radius * Math.sin(angle);
        return new LngLat(lng2, lat2);
    }


    @Test
    public void main() {

        File myObj = new File("outputFiles/route.geojson");
        myObj.delete();

        LngLat start = Constants.APPLETON_TOWER;
        // LngLat end = generateSecondPoint(start);
        LngLat end = new LngLat(-3.192473, 55.942617);
        //LngLat end = new LngLat(-3.192473, 55.946233);

        CompassDirection[] route = start.routeTo(end);
        //Reverse the route so that it starts at the start point
        LngLat next = start;
        try {
            File file = new File("outputFiles/route.geojson");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("{\"type\": \"Feature\",\"properties\": {\"marker-color\": \"#00ff00\"}, \"geometry\": { " +
                    "\"type\": \"Point\", " +
                    "\"coordinates\": [" + start.lng() + ", " + start.lat() + "] }},");
            for (CompassDirection direction : route) {
                next = next.nextPosition(direction);
                writer.write("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"Point\", " +
                        "\"coordinates\": [" + next.lng() + ", " + next.lat() + "] }},");
            }
            writer.write("{\"type\": \"Feature\",\"properties\": {\"marker-color\": \"#ff0000\"}, \"geometry\": { " +
                    "\"type\": \"Point\", " +
                    "\"coordinates\": [" + end.lng() + ", " + end.lat() + "] }}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
