package UnitTests.RouteCalculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import RouteCalculation.AreaSingleton;
import RouteCalculation.CompassDirection;
import RouteCalculation.LngLat;
import RouteCalculation.RouteCalculator;

public class RouteCalculatorUnitTest {
    private static final LngLat START = new LngLat(-3.186874, 55.944494);
    private static final LngLat GOAL = new LngLat(-3.190000, 55.950000);
    private static final LngLat NEXT_TARGET = new LngLat(-3.180000, 55.940000);
    private AreaSingleton mockSingleton;

    @Before
    public void setupSingleton() {
        mockSingleton = mock(AreaSingleton.class);
        AreaSingleton.setInstance(mockSingleton);

        // Default behavior: No no-fly zones
        LngLat[][] noNoFlyZones = { {} };
        when(mockSingleton.getNoFlyZones()).thenReturn(noNoFlyZones);
    }

    @Test
    public void testCalculateRoute_SimpleStraightLine() {
        CompassDirection[] route = RouteCalculator.calculateRoute(START, GOAL, null);

        assertNotNull("Route should not be null for a valid path", route);
        assertTrue("Route should contain at least one direction", route.length > 0);
        assertEquals("Route should end with a hover", CompassDirection.HOVER, route[route.length - 1]);
    }

    @Test
    public void testCalculateRoute_WithNextTarget() {
        CompassDirection[] route = RouteCalculator.calculateRoute(START, GOAL, NEXT_TARGET);

        assertNotNull("Route should not be null for a valid path with next target", route);
        assertTrue("Route should contain at least one direction", route.length > 0);
        assertEquals("Route should end with a hover", CompassDirection.HOVER, route[route.length - 1]);
    }

    @Test
    public void testCalculateRoute_NoFlyZoneDetour() {

        CompassDirection[] route_without_detour = RouteCalculator.calculateRoute(START, GOAL, null);

        LngLat[][] noFlyZones = {
                { new LngLat(-3.19, 55.945), new LngLat(-3.19, 55.946), new LngLat(-3.18, 55.946),
                        new LngLat(-3.18, 55.945) }
        };
        when(mockSingleton.getNoFlyZones()).thenReturn(noFlyZones);

        CompassDirection[] route = RouteCalculator.calculateRoute(START, GOAL, null);

        assertNotNull("Route should not be null for a valid path with no-fly zones", route);
        assertTrue("Route should contain at least one direction", route.length > 0);
        assertTrue("Diverted route should be different from the original route", route != route_without_detour);
        assertTrue("Route should contain directions to detour around the no-fly zone",
                route.length > route_without_detour.length);
    }

    @Test
    public void testCalculateRoute_InvalidStartPoint() {
        CompassDirection[] route = RouteCalculator.calculateRoute(null, GOAL, null);

        assertNull("Route should be null if start point is invalid", route);
    }

    @Test
    public void testCalculateRoute_InvalidEndPoint() {
        CompassDirection[] route = RouteCalculator.calculateRoute(START, null, null);

        assertNull("Route should be null if end point is invalid", route);
    }

    @Test
    public void testCalculateRoute_MultipleValidMoves() {
        CompassDirection[] route = RouteCalculator.calculateRoute(START, GOAL, NEXT_TARGET);

        assertNotNull("Route should not be null when there are multiple valid moves", route);
        assertTrue("Route should contain multiple steps", route.length > 1);
    }

}
