package uk.ac.ed.inf;

public class LngLat {
    public double longitude;
    public double latitude;
    public LngLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    private double triangleArea(LngLat p1, LngLat p2, LngLat p3){
        double a = p1.distanceTo(p2);
        double b = p2.distanceTo(p3);
        double c = p3.distanceTo(p1);
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    public boolean inCentralArea(){
        // Todo implement functionality to read a,b,c,d from the JSON file.
        LngLat a = new LngLat(-3.192473, 55.946233);
        LngLat b = new LngLat(-3.184319, 55.946233);
        LngLat c = new LngLat(-3.192473, 55.942617);
        LngLat d = new LngLat(-3.184319, 55.942617);
        // Use the triangle area method to check if the point is in the central area.
        double area = triangleArea(a, b, c) + triangleArea(c, d, b);
        double area1 = triangleArea(a, b, this);
        double area2 = triangleArea(b, c, this);
        double area3 = triangleArea(c, d, this);
        double area4 = triangleArea(d, a, this);
        return area == area1 + area2 + area3 + area4;
    }

    public double distanceTo(LngLat source){
        // Calculates the pythagorean distance between two points.
        return Math.sqrt(Math.pow(source.latitude - this.latitude, 2) + Math.pow(source.longitude - this.longitude, 2));
    }

    public boolean closeTo(LngLat source){
        // Finds the distance to the other source and checks if it is less than 0.00015.
        return distanceTo(source) < 0.00015;
    }

    public LngLat nextPosition(int direction){
        // Calculates the next position based on the direction.
        double radian = Math.toRadians(direction);
        double newLng = this.longitude + 0.00015 * Math.cos(radian);
        double newLat = this.latitude + 0.00015 * Math.sin(radian);
        return new LngLat(newLng, newLat);
    }

}