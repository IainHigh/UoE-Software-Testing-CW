package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;

public final class CentralAreaSingleton {
    private static CentralAreaSingleton instance ;
    public final double[][] centralAreaBorder;
    private CentralAreaSingleton() {
        try {
            URL url = new URL("https://ilp-rest.azurewebsites.net/centralArea");
            JSONRetriever retriever = new JSONRetriever();
            CentralAreaPoint[] centralBorder = retriever.getCentralArea(url);
            centralAreaBorder = new double[centralBorder.length][2];
            for (int i = 0; i < centralBorder.length; i++){
                centralAreaBorder[i][0] = centralBorder[i].longitude;
                centralAreaBorder[i][1] = centralBorder[i].latitude;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public static CentralAreaSingleton getInstance() {
        if (instance == null) {
            instance = new CentralAreaSingleton();
        }
        return instance;
    }
}
