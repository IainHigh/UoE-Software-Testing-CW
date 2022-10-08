package uk.ac.ed.inf;

import org.junit.Test;

public class testInNoFlyZone {

    @Test
    public void main(){
        LngLat point = new LngLat(		-3.1886, 	55.9433);
        System.out.println(point.inNoFlyZone());
    }
}
