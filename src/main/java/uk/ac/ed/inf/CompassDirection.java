package uk.ac.ed.inf;

public enum CompassDirection {
    E (0.0),
    ENE (22.5),
    NE (45.0),
    NNE (67.5),
    N (90.0),
    NNW (112.5),
    NW (135.0),
    WNW (157.5),
    W (180.0),
    WSW (202.5),
    SW (225.0),
    SSW (247.5),
    S (270.0),
    SSE (292.5),
    SE (315.0),
    ESE (337.5);

    private final double angle;
    CompassDirection(double angle) {
        this.angle = angle;
    }
    public double getAngle() {
        return this.angle;
    }
}
