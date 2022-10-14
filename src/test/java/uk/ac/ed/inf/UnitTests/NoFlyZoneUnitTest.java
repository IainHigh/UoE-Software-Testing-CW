package uk.ac.ed.inf.UnitTests;

import org.junit.Test;
import uk.ac.ed.inf.LngLat;

import static org.junit.Assert.assertTrue;

public class NoFlyZoneUnitTest {

    @Test
    public void testPointInNoFlyZone(){
        LngLat point = new LngLat(-3.1886,55.9433);
        assertTrue(point.inNoFlyZone());
    }
}
