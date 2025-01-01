package UnitTests;

import org.junit.Test;
import RouteCalculation.LngLat;

import java.util.Random;

import static org.junit.Assert.assertFalse;

public class CloseToUnitTest {
    @Test
    public void testClosePoint() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            double lng = random.nextDouble() * 360 - 180;
            double lat = random.nextDouble() * 180 - 90;
            LngLat point1 = new LngLat(lng, lat);
            LngLat point2 = generateCloseSecondPoint(point1);
            assert (point1.closeTo(point2));
        }
    }

    @Test
    public void testDistantPoint() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            double lng = random.nextDouble() * 360 - 180;
            double lat = random.nextDouble() * 180 - 90;
            LngLat point1 = new LngLat(lng, lat);
            LngLat point2 = generateDistantSecondPoint(point1);
            assertFalse(point1.closeTo(point2));
        }
    }

    @Test
    public void testNullPoint() {
        LngLat point1 = new LngLat(0, 0);
        assertFalse(point1.closeTo(null));
    }

    public LngLat generateCloseSecondPoint(LngLat p1) {
        // Generates a point that is within a radius of 0.00015 degrees of the first
        // point
        Random random = new Random();
        double radius = 0.00015 * random.nextDouble();
        double angle = random.nextDouble() * 2 * Math.PI;
        double lng2 = p1.getLng() + radius * Math.cos(angle);
        double lat2 = p1.getLat() + radius * Math.sin(angle);
        return new LngLat(lng2, lat2);
    }

    public LngLat generateDistantSecondPoint(LngLat p1) {
        // Generates a point that is outwith the radius of 0.00015 degrees of the first
        // point
        Random random = new Random();
        double radius = 0.00015 * random.nextDouble() + 0.00015;
        double angle = random.nextDouble() * 2 * Math.PI;
        double lng2 = p1.getLng() + radius * Math.cos(angle);
        double lat2 = p1.getLat() + radius * Math.sin(angle);
        return new LngLat(lng2, lat2);
    }
}
