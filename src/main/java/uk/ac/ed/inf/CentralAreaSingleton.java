package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance;
    private final double[][] centralAreaBorder;

    /**
     * Private constructor to prevent instantiation.
     * @param url The URL of the JSON file.
     */
    private CentralAreaSingleton(URL url) {
        JSONRetriever retriever = new JSONRetriever();
        centralAreaBorder = retriever.getCentralArea(url);
    }

    /**
     * Returns the instance of the singleton.
     * Overloaded method to allow for the use of a default URL.
     * @return The instance of the singleton.
     */
    public static CentralAreaSingleton getInstance() {
        if (instance == null) {
            try {
                instance = new CentralAreaSingleton(new URL("https://ilp-rest.azurewebsites.net/centralArea"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * Returns the instance of the singleton.
     * Overloaded method to allow for the use of a custom URL.
     * @param url The URL of the JSON file.
     * @return The instance of the singleton.
     */
    public static CentralAreaSingleton getInstance(URL url) {
        if (instance == null) {
            instance = new CentralAreaSingleton(url);
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
