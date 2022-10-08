package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

// TODO: Should probably rename this file as it is also used for the noFlyZones.

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance;
    private final double[][] centralAreaBorder;
    private final double[][][] noFlyZones;

    /**
     * Private constructor to prevent multiple instantiation.
     */
    private CentralAreaSingleton() {
        // TODO: URL should be passed in - this would break singleton pattern though so maybe have initialization function?
        var retriever = new JSONRetriever();
        try {
            URL centralAreaUrl = new URL("https://ilp-rest.azurewebsites.net/centralArea");
            centralAreaBorder = retriever.getCentralArea(centralAreaUrl);
            URL noFlyZonesUrl = new URL("https://ilp-rest.azurewebsites.net/noFlyZones");
            noFlyZones = retriever.getNoFlyZones(noFlyZonesUrl);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The instance of the singleton.
     */
    public static CentralAreaSingleton getInstance() {
        if (instance == null) {
            instance = new CentralAreaSingleton();
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
