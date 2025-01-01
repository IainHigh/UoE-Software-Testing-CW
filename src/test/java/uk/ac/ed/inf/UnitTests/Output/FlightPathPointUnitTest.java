package uk.ac.ed.inf.UnitTests.Output;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Output.FlightPathPoint;

public class FlightPathPointUnitTest {
        @Test
    public void testConstructorAndGetters() {
        FlightPathPoint point = new FlightPathPoint(
            "ORD12345",
            -3.186874,
            55.944494,
            45.0,
            -3.185874,
            55.945494,
            120
        );

        // Check starting coordinates
        double[] startCoords = point.getStartingCoordinates();
        assertEquals("Starting longitude mismatch", -3.186874, startCoords[0], 0.0001);
        assertEquals("Starting latitude mismatch", 55.944494, startCoords[1], 0.0001);

        // Check destination coordinates
        double[] destinationCoords = point.getDestinationCoordinates();
        assertEquals("Destination longitude mismatch", -3.185874, destinationCoords[0], 0.0001);
        assertEquals("Destination latitude mismatch", 55.945494, destinationCoords[1], 0.0001);
    }

    @Test
    public void testHoverPoint() {
        FlightPathPoint point = new FlightPathPoint(
            "ORD12345",
            -3.186874,
            55.944494,
            null,
            -3.186874,
            55.944494,
            120
        );

        // Check starting and destination coordinates are the same
        double[] startCoords = point.getStartingCoordinates();
        double[] destinationCoords = point.getDestinationCoordinates();
        assertArrayEquals("Hover point coordinates mismatch", startCoords, destinationCoords, 0.0001);

        // Check JSON output
        String expectedJson = "{" +
                "\"orderNumber\": \"ORD12345\"," +
                " \"fromLongitude\": -3.186874," +
                " \"fromLatitude\": 55.944494," +
                " \"angle\": null," +
                " \"toLongitude\": -3.186874," +
                " \"toLatitude\": 55.944494," +
                " \"ticksSinceStartOfCalculation\": 120}";
        assertEquals("Hover point JSON mismatch", expectedJson, point.toJson());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLongitude() {
        new FlightPathPoint("ORD12345", -200.0, 55.944494, 45.0, -3.185874, 55.945494, 120);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLatitude() {
        new FlightPathPoint("ORD12345", -3.186874, 100.0, 45.0, -3.185874, 55.945494, 120);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAngle() {
        new FlightPathPoint("ORD12345", -3.186874, 55.944494, 400.0, -3.185874, 55.945494, 120);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTicks() {
        new FlightPathPoint("ORD12345", -3.186874, 55.944494, 45.0, -3.185874, 55.945494, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHoverWithDifferentCoordinates() {
        new FlightPathPoint("ORD12345", -3.186874, 55.944494, null, -3.185874, 55.945494, 120);
    }

    @Test
    public void testToJson() {
        FlightPathPoint point = new FlightPathPoint(
            "ORD12345",
            -3.186874,
            55.944494,
            90.0,
            -3.186874,
            56.944494,
            150
        );

        String expectedJson = "{" +
                "\"orderNumber\": \"ORD12345\"," +
                " \"fromLongitude\": -3.186874," +
                " \"fromLatitude\": 55.944494," +
                " \"angle\": 90.0," +
                " \"toLongitude\": -3.186874," +
                " \"toLatitude\": 56.944494," +
                " \"ticksSinceStartOfCalculation\": 150}";
        assertEquals("JSON output mismatch", expectedJson, point.toJson());
    }
    
}
