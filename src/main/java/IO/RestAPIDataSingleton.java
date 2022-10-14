package IO;

import uk.ac.ed.inf.LngLat;

import java.net.URL;

public final class RestAPIDataSingleton {
    private static RestAPIDataSingleton instance;
    private LngLat[] centralAreaBorder;
    private LngLat[][] noFlyZones;

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
    public static RestAPIDataSingleton getInstance() {
        if (instance == null) {
            instance = new RestAPIDataSingleton();
        }
        return instance;
    }

    /**
     * Accessor method for the central area border.
     * @return The central area border.
     */
    public LngLat[] getCentralAreaBorder() {
        return centralAreaBorder;
    }

    /**
     * Accessor method for the no-fly zones.
     * @return The no-fly zones.
     */
    public LngLat[][] getNoFlyZones() {
        return noFlyZones;
    }
}
