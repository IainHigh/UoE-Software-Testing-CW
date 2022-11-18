package uk.ac.ed.inf.UnitTests;

import org.junit.Test;
import uk.ac.ed.inf.CompassDirection;
import uk.ac.ed.inf.LngLat;

import java.util.Random;

public class NextPositionUnitTest {

    @Test
    public void testMove(){
        // Generate a random LngLat point.
        Random rand = new Random();
        double lat = rand.nextDouble() * 180 - 90;
        double lng = rand.nextDouble() * 360 - 180;
        LngLat point = new LngLat(lng, lat);

        // Go through all the possible directions and check that the distance and angle are correct.
        for (CompassDirection dir : CompassDirection.valuesNoHover()) {
            if (dir == CompassDirection.HOVER) continue;
            LngLat nextPoint = point.nextPosition(dir);
            assert(calculateAngle(point, nextPoint) == dir.getAngle());
            assert(calculateDistance(point, nextPoint) > 0.00015 - 10e-12);
            assert(calculateDistance(point, nextPoint) < 0.00015 + 10e-12);
        }
    }

    @Test
    public void testHover(){
        Random rand = new Random();
        for (int i = 0; i < 100; i++){
            double lat = rand.nextDouble() * 180 - 90;
            double lng = rand.nextDouble() * 360 - 180;
            LngLat point = new LngLat(lng, lat);
            LngLat hoverPoint = point.nextPosition(null);
            assert(hoverPoint.lat() == point.lat());
        }
    }

    private double calculateAngle(LngLat p1, LngLat p2) {
        double angle = Math.atan2(p2.lat() - p1.lat(), p2.lng() - p1.lng());
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return Math.round(((angle*180)/Math.PI) * 10000 ) / 10000.0;
    }

    private double calculateDistance(LngLat p1, LngLat p2) {
        return p1.distanceTo(p2);
    }
}
