package uk.ac.ed.inf.UnitTests;

import org.junit.Test;
import uk.ac.ed.inf.LngLat;

import java.util.Random;

public class DistanceToUnitTest {
    @Test
    public void testDistanceTo() {
        LngLat p1 = new LngLat(0, 0);
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            double radius = random.nextDouble();
            double angle = random.nextDouble() * 2 * Math.PI;
            double lng2 = p1.lng() + radius * Math.cos(angle);
            double lat2 = p1.lat() + radius * Math.sin(angle);
            LngLat p2 = new LngLat(lng2, lat2);
            assert(p1.distanceTo(p2) == p2.distanceTo(p1));
            assert((p1.distanceTo(p2) - radius) < 10e-12);
        }
    }

    @Test
    public void testDistanceToSelf() {
        LngLat p1 = new LngLat(0, 0);
        assert(p1.distanceTo(p1) == 0);
    }

    @Test
    public void testDistanceToNull() {
        LngLat p1 = new LngLat(0, 0);
        assert(p1.distanceTo(null) == Double.POSITIVE_INFINITY);
    }
}
