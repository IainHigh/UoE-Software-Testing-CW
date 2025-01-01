package UnitTests.RouteCalculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import RouteCalculation.CompassDirection;

public class CompassDirectionUnitTest {

    // -----------------------------------------------------------------------------------------------
    // TEST GET ANGLE
    // -----------------------------------------------------------------------------------------------

    @Test
    public void testGetAngle_ForAllDirections() {
        assertEquals("Angle for HOVER should be null", null, CompassDirection.HOVER.getAngle());
        assertEquals("Angle for E should be 0.0", 0.0, CompassDirection.E.getAngle(), 0.0001);
        assertEquals("Angle for NE should be 45.0", 45.0, CompassDirection.NE.getAngle(), 0.0001);
        assertEquals("Angle for N should be 90.0", 90.0, CompassDirection.N.getAngle(), 0.0001);
        assertEquals("Angle for W should be 180.0", 180.0, CompassDirection.W.getAngle(), 0.0001);
        assertEquals("Angle for S should be 270.0", 270.0, CompassDirection.S.getAngle(), 0.0001);
        assertEquals("Angle for SE should be 315.0", 315.0, CompassDirection.SE.getAngle(), 0.0001);
    }

    // -----------------------------------------------------------------------------------------------
    // TEST VALUES NO HOVER
    // -----------------------------------------------------------------------------------------------

    @Test
    public void testValuesNoHover_ExcludesHover() {
        CompassDirection[] directions = CompassDirection.valuesNoHover();
        assertFalse("HOVER should not be included in valuesNoHover",
                java.util.Arrays.asList(directions).contains(CompassDirection.HOVER));
        assertEquals("valuesNoHover should have 16 directions", 16, directions.length);
    }

    @Test
    public void testValuesNoHover_AllDirectionsIncluded() {
        CompassDirection[] directions = CompassDirection.valuesNoHover();
        assertTrue("E should be included", java.util.Arrays.asList(directions).contains(CompassDirection.E));
        assertTrue("N should be included", java.util.Arrays.asList(directions).contains(CompassDirection.N));
        assertTrue("W should be included", java.util.Arrays.asList(directions).contains(CompassDirection.W));
        assertTrue("S should be included", java.util.Arrays.asList(directions).contains(CompassDirection.S));
        assertTrue("SE should be included", java.util.Arrays.asList(directions).contains(CompassDirection.SE));
    }

    // -----------------------------------------------------------------------------------------------
    // VALIDATE ORDER AND ANGLES
    // -----------------------------------------------------------------------------------------------

    @Test
    public void testCompassDirectionOrderAndAngles() {
        CompassDirection[] directions = CompassDirection.values();
        double previousAngle = -1; // Start with min to ensure order is clockwise
        for (CompassDirection direction : directions) {
            if (direction != CompassDirection.HOVER) {
                assertTrue("Angles should increase in clockwise order", direction.getAngle() > previousAngle);
                previousAngle = direction.getAngle();
            }
        }
    }

    @Test
    public void testHoverIsOnlyNullAngle() {
        for (CompassDirection direction : CompassDirection.values()) {
            if (direction == CompassDirection.HOVER) {
                assertNull("Hover angle should be null", direction.getAngle());
            } else {
                assertNotNull("Non-hover direction angle should not be null", direction.getAngle());
            }
        }
    }

    // -----------------------------------------------------------------------------------------------
    // GENERAL TESTS
    // -----------------------------------------------------------------------------------------------

    @Test
    public void testTotalValues() {
        assertEquals("Total number of compass directions (including hover) should be 17",
                17, CompassDirection.values().length);
    }

    @Test
    public void testValidAngleRange() {
        for (CompassDirection direction : CompassDirection.valuesNoHover()) {
            assertTrue("Angle must be within 0 to 360 degrees",
                    direction.getAngle() >= 0.0 && direction.getAngle() <= 360.0);
        }
    }
}
