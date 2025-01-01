package UnitTests.RouteCalculation;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import RouteCalculation.AreaSingleton;
import RouteCalculation.CompassDirection;
import RouteCalculation.LngLat;

public class LngLatUnitTest {
    private static final LngLat APPLETON = new LngLat(-3.186874, 55.944494);
    private static final LngLat FORREST = new LngLat(-3.192473, 55.946233);
    private static final LngLat BUCCLEUCH_BUS = new LngLat(-3.184319, 55.942617);
    private static final double ACCEPTANCE_THRESHOLD = 0.0000000000001;

    // -----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------------

    @Test
    // LngLat constructor assigns values
    public void lngLat_constructor() {
        double lng = 3.186874;
        double lat = 55.944494;

        LngLat point = new LngLat(lng, lat);

        assertEquals("Longitude not assigned", lng, point.getLng(), ACCEPTANCE_THRESHOLD);
        assertEquals("Latitude not assigned", lat, point.getLat(), ACCEPTANCE_THRESHOLD);
    }

    @Test
    // LngLat constructor throws exception for longitude above 180
    public void lngLat_constructor_invalidLongitude() {
        double lng = 181;
        double lat = 55.944494;
        assertThrows("Invalid longitude not caught", IllegalArgumentException.class,
                () -> new LngLat(lng, lat));
    }

    @Test
    // LngLat constructor throws exception for longitude below -180
    public void lngLat_constructor_invalidLongitudeLower() {
        double lng = -181;
        double lat = 55.944494;
        assertThrows("Invalid longitude not caught", IllegalArgumentException.class,
                () -> new LngLat(lng, lat));
    }

    @Test
    // LngLat constructor throws exception for invalid latitude
    public void lngLat_constructor_invalidLatitude() {
        double lng = 3.186874;
        double lat = 91;
        assertThrows("Invalid latitude not caught", IllegalArgumentException.class,
                () -> new LngLat(lng, lat));
    }

    @Test
    // LngLat constructor throws exception invalid latitude lower
    public void lngLat_constructor_invalidLatitudeLower() {
        double lng = 3.186874;
        double lat = -91;
        assertThrows("Invalid latitude not caught", IllegalArgumentException.class,
                () -> new LngLat(lng, lat));
    }

    // -----------------------------------------------------------------------------------------------
    // DISTANCE TO
    // -----------------------------------------------------------------------------------------------

    @Test
    // Correct distance between Appleton and Forrester Hill
    public void distanceTo_appletonAndForrest() {
        double actualDistance = APPLETON.distanceTo(FORREST);
        double expectedDistance = 0.005862842484665912; // manually computed
        assertEquals("Distance between Appleton and Forrest is incorrect", expectedDistance, actualDistance,
                ACCEPTANCE_THRESHOLD);
    }

    @Test
    // Distance between the same point is 0
    public void distanceTo_zeroDistanceOnSameLocation() {
        double actualDistance = APPLETON.distanceTo(APPLETON);
        assertEquals("Distance between the same point is not 0", 0.0, actualDistance, ACCEPTANCE_THRESHOLD);
    }

    @Test
    // Distance to null point is infinity
    public void distanceTo_nullPoint() {
        double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
        assertEquals("Distance between null point is infinite", POSITIVE_INFINITY, APPLETON.distanceTo(null),
                ACCEPTANCE_THRESHOLD);
    }

    // -----------------------------------------------------------------------------------------------
    // CLOSE TO
    // -----------------------------------------------------------------------------------------------

    @Test
    // Same point is close to itself
    public void closeTo_pointCloseToItself() {
        assertTrue("Point is not close to itself", APPLETON.closeTo(APPLETON));
    }

    @Test
    // Appleton Tower is not 'close to' Buccleuch Bus
    public void closeTo_appletonNotCloseToBuccleuch() {
        assertFalse("Appleton Tower is close to Buccleuch Bus", APPLETON.closeTo(BUCCLEUCH_BUS));
    }

    @Test
    // Point should not be close to null
    public void closeTo_pointNotCloseToNull() {
        assertFalse("Point is close to null", APPLETON.closeTo(null));
    }

    @Test
    // Point exactly 0.00015 degrees away should not be classed as close
    public void closeTo_pointExactlyThresholdAway() {
        LngLat thresholdAway = new LngLat(APPLETON.getLng() + 0.00015, APPLETON.getLat());
        assertFalse("Point is close to point exactly 0.00015 degrees away", APPLETON.closeTo(thresholdAway));
    }

    // -----------------------------------------------------------------------------------------------
    // NEXT MOVE
    // -----------------------------------------------------------------------------------------------

    @Test
    // Moving at degree 0 (E) only displaces lng
    public void nextMove_correctEastDisplacement() {
        LngLat newPosition = APPLETON.nextPosition(CompassDirection.E);
        assertEquals("Moving at degree 0 (E) should only displace lng", APPLETON.getLng() + 0.00015,
                newPosition.getLng(), ACCEPTANCE_THRESHOLD);
        assertEquals("Moving at degree 0 (E) should only displace lat", APPLETON.getLat(), newPosition.getLat(),
                ACCEPTANCE_THRESHOLD);
    }

    @Test
    // Moving at degree 180 (W) only displaces lng
    public void nextMove_correctWestDisplacement() {
        LngLat newPosition = FORREST.nextPosition(CompassDirection.W);
        assertEquals("Moving at degree 180 (W) should only displace lng", FORREST.getLng() - 0.00015,
                newPosition.getLng(), ACCEPTANCE_THRESHOLD);
        assertEquals("Moving at degree 180 (W) should only displace lat", FORREST.getLat(), newPosition.getLat(),
                ACCEPTANCE_THRESHOLD);
    }

    @Test
    // Moving at degree 90 (N) only displaces lat
    public void nextMove_correctNorthDisplacement() {
        LngLat newPosition = BUCCLEUCH_BUS.nextPosition(CompassDirection.N);
        assertEquals("Moving at degree 90 (N) should only displace lat", BUCCLEUCH_BUS.getLat() + 0.00015,
                newPosition.getLat(),
                ACCEPTANCE_THRESHOLD);
        assertEquals("Moving at degree 90 (N) should only displace lng", BUCCLEUCH_BUS.getLng(), newPosition.getLng(),
                ACCEPTANCE_THRESHOLD);
    }

    @Test
    // Moving at degree 270 (S) only displaces lat
    public void nextMove_correctSouthDisplacement() {
        LngLat newPosition = APPLETON.nextPosition(CompassDirection.S);
        assertEquals("Moving at degree 270 (S) should only displace lat", APPLETON.getLat() - 0.00015,
                newPosition.getLat(), ACCEPTANCE_THRESHOLD);
        assertEquals("Moving at degree 270 (S) should only displace lng", APPLETON.getLng(), newPosition.getLng(),
                ACCEPTANCE_THRESHOLD);
    }

    // -----------------------------------------------------------------------------------------------
    // IN-ZONE TESTS
    // -----------------------------------------------------------------------------------------------
    @Test
    public void testInZone_PointInsideZone() {
        LngLat[] zone = {
            new LngLat(-3.187, 55.944),
            new LngLat(-3.185, 55.944),
            new LngLat(-3.186, 55.945)
        };
        LngLat point = new LngLat(-3.1865, 55.9445);
        assertTrue("Point should be inside the zone", point.inZone(zone));
    }

    @Test
    public void testInZone_PointOnBorder() {
        LngLat[] zone = {
            new LngLat(-3.187, 55.944),
            new LngLat(-3.185, 55.944),
            new LngLat(-3.186, 55.945)
        };
        LngLat point = new LngLat(-3.187, 55.944); // On border
        assertFalse("Point on border should not be inside the zone", point.inZone(zone));
    }

    @Test
    public void testInNoFlyZone_LineIntersectsZone() {
        LngLat start = new LngLat(-3.187, 55.944);
        LngLat end = new LngLat(-3.185, 55.945);
        AreaSingleton mockSingleton = mock(AreaSingleton.class);
        when(mockSingleton.getNoFlyZones()).thenReturn(new LngLat[][]{
            {
                new LngLat(-3.186, 55.944),
                new LngLat(-3.185, 55.944),
                new LngLat(-3.1855, 55.945)
            }
        });

        AreaSingleton.setInstance(mockSingleton);

        assertTrue("Line should intersect the no-fly zone", start.inNoFlyZone(end));
    }


    // -----------------------------------------------------------------------------------------------
    // MISCILANEOUS TESTS
    // -----------------------------------------------------------------------------------------------


    @Test
    public void testMove() {
        // Generate a random LngLat point.
        Random rand = new Random();
        double lat = rand.nextDouble() * 180 - 90;
        double lng = rand.nextDouble() * 360 - 180;
        LngLat point = new LngLat(lng, lat);

        // Go through all the possible directions and check that the distance and angle
        // are correct.
        for (CompassDirection dir : CompassDirection.valuesNoHover()) {
            if (dir == CompassDirection.HOVER)
                continue;
            LngLat nextPoint = point.nextPosition(dir);
            assert (calculateAngle(point, nextPoint) == dir.getAngle());
            assert (calculateDistance(point, nextPoint) > 0.00015 - 10e-12);
            assert (calculateDistance(point, nextPoint) < 0.00015 + 10e-12);
        }
    }

    @Test
    public void testHover() {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            double lat = rand.nextDouble() * 180 - 90;
            double lng = rand.nextDouble() * 360 - 180;
            LngLat point = new LngLat(lng, lat);
            LngLat hoverPoint = point.nextPosition(null);
            assert (hoverPoint.getLat() == point.getLat());
            assert (hoverPoint.getLng() == point.getLng());
        }
    }

    private double calculateAngle(LngLat p1, LngLat p2) {
        double angle = Math.atan2(p2.getLat() - p1.getLat(), p2.getLng() - p1.getLng());
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return Math.round(((angle * 180) / Math.PI) * 10000) / 10000.0;
    }

    private double calculateDistance(LngLat p1, LngLat p2) {
        return p1.distanceTo(p2);
    }
}
