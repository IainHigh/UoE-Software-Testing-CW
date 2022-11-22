package uk.ac.ed.inf;

/**
 * An enum for storing the 16 compass directions (and hover).
 * Each compass direction has a corresponding angle in degrees. Starting at east (0 degrees) and going anti-clockwise.
 */
public enum CompassDirection {
    HOVER(null), // Although hover could be implemented just as a null direction, I find it more readable.
    E(0.0),
    ENE(22.5),
    NE(45.0),
    NNE(67.5),
    N(90.0),
    NNW(112.5),
    NW(135.0),
    WNW(157.5),
    W(180.0),
    WSW(202.5),
    SW(225.0),
    SSW(247.5),
    S(270.0),
    SSE(292.5),
    SE(315.0),
    ESE(337.5);

    private final Double angle;

    CompassDirection(Double angle) {
        this.angle = angle;
    }

    public Double getAngle() {
        return this.angle;
    }

    /**
     * @return The list of compass directions, excluding hover.
     */
    public static CompassDirection[] valuesNoHover() {
        return new CompassDirection[]{E, ENE, NE, NNE, N, NNW, NW, WNW, W, WSW, SW, SSW, S, SSE, SE, ESE};
    }
}
