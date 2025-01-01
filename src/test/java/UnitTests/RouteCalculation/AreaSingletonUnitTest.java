package UnitTests.RouteCalculation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import RouteCalculation.AreaSingleton;
import RouteCalculation.LngLat;

public class AreaSingletonUnitTest {
    private AreaSingleton areaSingleton;

    @Before
    public void setUp() {
        areaSingleton = AreaSingleton.getInstance();
    }

    @Test
    public void testSingletonBehavior() {
        AreaSingleton instance1 = AreaSingleton.getInstance();
        AreaSingleton instance2 = AreaSingleton.getInstance();

        assertSame("Singleton instances should be the same", instance1, instance2);
    }

    @Test
    public void testSetURLs_ValidCentralAreaAndNoFlyZone() throws MalformedURLException {
        URL centralAreaUrl = new URL("https://ilp-rest-2024.azurewebsites.net/centralArea");
        URL noFlyZonesUrl = new URL("https://ilp-rest-2024.azurewebsites.net/noFlyZones");

        // Mock central area and no-fly zone responses
        areaSingleton.setURLs(centralAreaUrl, noFlyZonesUrl);

        // Validate central area border
        LngLat[] centralAreaBorder = areaSingleton.getCentralAreaBorder();
        assertNotNull("Central area border should not be null", centralAreaBorder);
        assertTrue("Central area border should have points", centralAreaBorder.length > 0);

        // Validate no-fly zones
        LngLat[][] noFlyZones = areaSingleton.getNoFlyZones();
        assertNotNull("No-fly zones should not be null", noFlyZones);
        assertTrue("No-fly zones should contain at least one zone", noFlyZones.length > 0);
        assertTrue("Each no-fly zone should contain points", noFlyZones[0].length > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testSetURLs_InvalidCentralAreaURL() throws MalformedURLException {
        URL invalidCentralAreaUrl = new URL("https://ilp-rest-2024.azurewebsites.net/invalidCentralArea");
        URL noFlyZonesUrl = new URL("https://ilp-rest-2024.azurewebsites.net/noFlyZones");

        // Simulate invalid central area URL
        areaSingleton.setURLs(invalidCentralAreaUrl, noFlyZonesUrl);
    }

    @Test(expected = RuntimeException.class)
    public void testSetURLs_InvalidNoFlyZoneURL() throws MalformedURLException {
        URL centralAreaUrl = new URL("https://ilp-rest-2024.azurewebsites.net/centralArea");
        URL invalidNoFlyZonesUrl = new URL("https://ilp-rest-2024.azurewebsites.net/invalidNoFlyZones");

        // Simulate invalid no-fly zone URL
        areaSingleton.setURLs(centralAreaUrl, invalidNoFlyZonesUrl);
    }

    @Test
    public void testCentralAreaBorder_NullBeforeSetURLs() {
        assertNull("Central area border should be null before setURLs is called", areaSingleton.getCentralAreaBorder());
    }

    @Test
    public void testNoFlyZones_NullBeforeSetURLs() {
        assertNull("No-fly zones should be null before setURLs is called", areaSingleton.getNoFlyZones());
    }

    @Test
    public void testSetInstance() {
        AreaSingleton mockInstance = new AreaSingleton();
        AreaSingleton.setInstance(mockInstance);
        assertSame("setInstance should replace the singleton instance", mockInstance, AreaSingleton.getInstance());
    }

}
