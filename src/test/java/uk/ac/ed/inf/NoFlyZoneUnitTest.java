package uk.ac.ed.inf;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NoFlyZoneUnitTest {

    @Test
    public void testPointInNoFlyZone(){
        LngLat point = new LngLat(-3.1886,55.9433);
        assertTrue(point.inNoFlyZone());
    }
}
