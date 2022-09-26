package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance ;
    public double[][] centralAreaBorder;
    private CentralAreaSingleton(URL url) {
        JSONRetriever retriever = new JSONRetriever();
        CentralAreaPoint[] centralBorder = retriever.getCentralArea(url);
        centralAreaBorder = new double[centralBorder.length][2];
        for (int i = 0; i < centralBorder.length; i++){
            centralAreaBorder[i][0] = centralBorder[i].longitude;
            centralAreaBorder[i][1] = centralBorder[i].latitude;
        }
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
}
