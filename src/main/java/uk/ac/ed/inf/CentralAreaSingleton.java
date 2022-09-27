package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance ;
    private final double[][] centralAreaBorder;
    private CentralAreaSingleton(URL url) {
        JSONRetriever retriever = new JSONRetriever();
        centralAreaBorder = retriever.getCentralArea(url);
    }
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

    public static CentralAreaSingleton getInstance(URL url) {
        if (instance == null) {
            instance = new CentralAreaSingleton(url);
        }
        return instance;
    }
    public double[][] getCentralAreaBorder() {
        return centralAreaBorder;
    }
}
