package IntegrationTests;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import RouteCalculation.*;

public class RouteCalculationIntegrationTest {
    private AreaSingleton areaSingleton;

    @Before
    public void setUp() throws Exception {
        // Reset AreaSingleton
        AreaSingleton.resetInstance();
        areaSingleton = AreaSingleton.getInstance();

        // Mock central area and no-fly zone URLs
        URL centralAreaUrl = new URL("https://ilp-rest-2024.azurewebsites.net/centralArea");
        URL noFlyZonesUrl = new URL("https://ilp-rest-2024.azurewebsites.net/noFlyZones");

        // Load mocked central area and no-fly zones
        areaSingleton.setURLs(centralAreaUrl, noFlyZonesUrl);
    }

    @After
    public void tearDown() {
        AreaSingleton.resetInstance();
    }

    @Test
    public void testLngLatInCentralArea() {
        LngLat pointInside = new LngLat(-3.1865, 55.9445);
        LngLat pointOutside = new LngLat(-3.2000, 55.9400);

        assertTrue("Point should be inside the central area", pointInside.inCentralArea());
        assertFalse("Point should be outside the central area", pointOutside.inCentralArea());
    }

    @Test
    public void testLngLatInNoFlyZone() {
        LngLat startPoint = new LngLat(-3.185, 55.945);
        LngLat pointInNoFlyZone = new LngLat(-3.188612, 55.943909);
        assertFalse("Start point should not be in a no-fly zone", startPoint.inNoFlyZone(null));
        assertTrue("Point should be in a no-fly zone", pointInNoFlyZone.inNoFlyZone(startPoint));
    }

    @Test
    public void testDistanceAndCloseToMethods() {
        LngLat pointA = new LngLat(-3.186, 55.944);
        LngLat pointB = new LngLat(-3.186, 55.9441);
        /// DISTANCE_TOLERANCE = 0.00015;
        double expectedDistance = pointA.distanceTo(pointB);
        assertEquals("Distance between points should match", expectedDistance, 0.0001, 0.00001);

        double reverseExpectedDistance = pointB.distanceTo(pointA);
        assertEquals("Distance between points should match", reverseExpectedDistance, 0.0001, 0.00001);

        assertEquals("Distance to self should be 0", 0, pointA.distanceTo(pointA), 0.00001);
        assertEquals("Distance to self should be 0", 0, pointB.distanceTo(pointB), 0.00001);

        assertTrue("Points should be considered close", pointA.closeTo(pointB));
        assertTrue("Points should be considered close", pointB.closeTo(pointA));
    }

    @Test
    public void testNextPosition() {
        LngLat startPoint = new LngLat(-3.186, 55.944);
        LngLat expectedPoint = new LngLat(-3.186, 55.944 + LngLat.getLengthOfMove());

        LngLat nextPoint = startPoint.nextPosition(CompassDirection.N);
        assertEquals("Next position should match expected", expectedPoint.getLat(), nextPoint.getLat(), 0.00001);
        assertEquals("Longitude should remain the same for northward movement", expectedPoint.getLng(),
                nextPoint.getLng(), 0.00001);
    }

    @Test
    public void testRouteCalculation_SimplePath() {
        LngLat startPoint = new LngLat(-3.186, 55.944);
        LngLat endPoint = new LngLat(-3.186, 55.945);

        CompassDirection[] route = RouteCalculator.calculateRoute(startPoint, endPoint, null);
        assertNotNull("Route should not be null", route);
        assertTrue("Route should contain directions", route.length > 0);
        assertEquals("Route should end with a hover", CompassDirection.HOVER, route[route.length - 1]);
    }

    @Test
    public void testRouteCalculation_WithObstacles() {
        LngLat startPoint = new LngLat(-3.185, 55.945);
        LngLat endPoint = new LngLat(-3.187, 55.947);

        CompassDirection[] route = RouteCalculator.calculateRoute(startPoint, endPoint, null);

        assertNotNull("Route should not be null", route);
        assertTrue("Route should consider obstacles", route.length > 0);
        assertEquals("Route should end with a hover", CompassDirection.HOVER, route[route.length - 1]);
    }

    @Test
    public void testAreaSingletonIntegration() {
        LngLat[] centralAreaBorder = areaSingleton.getCentralAreaBorder();
        assertNotNull("Central area border should not be null", centralAreaBorder);
        assertTrue("Central area border should have coordinates", centralAreaBorder.length > 0);

        LngLat[][] noFlyZones = areaSingleton.getNoFlyZones();
        assertNotNull("No-fly zones should not be null", noFlyZones);
        assertTrue("No-fly zones should contain at least one zone", noFlyZones.length > 0);
    }

    @Test
    public void testLngLatNumberOfMoves() {
        LngLat startPoint = new LngLat(-3.186, 55.944);
        LngLat endPoint = new LngLat(-3.186, 55.946);

        int numberOfMoves = startPoint.numberOfMovesTo(endPoint);
        assertTrue("Number of moves should be greater than 0", numberOfMoves > 0);
    }

    @Test
    public void testComplexRouteCalculation() {
        LngLat startPoint = new LngLat(-3.185, 55.945);
        LngLat endPoint = new LngLat(-3.190, 55.950);
        LngLat nextTarget = new LngLat(-3.191, 55.951);

        CompassDirection[] route = RouteCalculator.calculateRoute(startPoint, endPoint, nextTarget);

        assertNotNull("Route should not be null", route);
        assertTrue("Route should consider all targets", route.length > 0);
        assertEquals("Route should end with a hover", CompassDirection.HOVER, route[route.length - 1]);
    }
}
