package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance;
    private final double[][] centralAreaBorder;

    /**
     * Private constructor to prevent multiple instantiation.
     */
    private CentralAreaSingleton() {
        var retriever = new JSONRetriever();
        try {
            URL url = new URL("https://ilp-rest.azurewebsites.net/centralArea");
            centralAreaBorder = retriever.getCentralArea(url);
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
}
