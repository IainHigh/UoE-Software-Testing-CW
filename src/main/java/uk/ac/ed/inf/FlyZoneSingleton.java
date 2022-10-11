package uk.ac.ed.inf;

import java.net.URL;

public final class FlyZoneSingleton {
    private static FlyZoneSingleton instance;
    private double[][] centralAreaBorder;
    private double[][][] noFlyZones;

    /**
     * Pass in the URLs of the central area border and the no-fly zones.
     * @param centralAreaUrl The URL of the central area border.
     * @param noFlyZonesUrl The URL of the no-fly zones.
     */
    public void setURLs(URL centralAreaUrl, URL noFlyZonesUrl) {
        var retriever = new JSONRetriever();
        centralAreaBorder = retriever.getCentralArea(centralAreaUrl);
        noFlyZones = retriever.getNoFlyZones(noFlyZonesUrl);
    }

    /**
     * @return The instance of the singleton.
     */
    public static FlyZoneSingleton getInstance() {
        if (instance == null) {
            instance = new FlyZoneSingleton();
        }
        return instance;
    }

    /**
     * Accessor method for the central area border.
     * @return The central area border.
     */
    public double[][] getCentralAreaBorder() {
        return centralAreaBorder;
    }

    /**
     * Accessor method for the no-fly zones.
     * @return The no-fly zones.
     */
    public double[][][] getNoFlyZones() { return noFlyZones; }
}
