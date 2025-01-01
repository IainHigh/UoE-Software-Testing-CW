package RouteCalculation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Singleton used to access and store the no-fly zones and central area border.
 * This is stored as a singleton to avoid having to deserialize the JSON file
 * multiple times.
 */
public class AreaSingleton {
    private static AreaSingleton instance;
    private LngLat[] centralAreaBorder;
    private LngLat[][] noFlyZones;

    /**
     * Public method for getting the instance of the singleton.
     * Synchronised to ensure that only one instance is created.
     *
     * @return The instance of the singleton.
     */
    public static synchronized AreaSingleton getInstance() {
        if (instance == null) {
            instance = new AreaSingleton();
        }
        return instance;
    }

    /**
     * Pass in the URLs of the central area border and the no-fly zones.
     * Once URLs are passed in the data is retrieved from the REST API.
     * Then the data is stored in the singleton and can be accessed from anywhere in
     * the program without having to
     * access the REST API again.
     *
     * @param centralAreaUrl The URL of the JSON file containing the coordinates of
     *                       the central area border.
     * @param noFlyZonesUrl  The URL of the JSON file containing the list of no-fly
     *                       zones.
     */
    public void setURLs(URL centralAreaUrl, URL noFlyZonesUrl) {
        centralAreaBorder = deserializeCentralArea(centralAreaUrl);
        noFlyZones = deserializeNoFlyZone(noFlyZonesUrl);
    }

    /**
     * Retrieves the central area from the JSON file.
     *
     * @param url The URL of the JSON file containing the coordinates of the central
     *            area border.
     * @return An array of LngLat objects representing the border of the central
     *         area.
     */
    private LngLat[] deserializeCentralArea(URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Deserialize into the wrapper class
            CentralArea centralArea = mapper.readValue(url, CentralArea.class);

            // Extract the vertices list and convert it to an array
            List<LngLat> vertices = centralArea.getVertices();
            return vertices.toArray(new LngLat[0]);
        } catch (IOException e) {
            System.err.println("Error retrieving central area from REST API.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the no-fly zones from the JSON file.
     * Since the no-fly zones are stored in the JSON file as a list of lists of
     * coordinates, this method deserializes
     * the file into a list of NoFlyZone objects. Then converts the NoFlyZone
     * objects into an array of LngLat arrays.
     *
     * @param url The URL of the JSON file.
     * @return An array of no-fly zones, where each no-fly zone is an array of
     *         LngLat points.
     */
    private LngLat[][] deserializeNoFlyZone(URL url) {
        NoFlyZone[] noFlyZoneObjectArray;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Deserialize into an array of NoFlyZone objects
            noFlyZoneObjectArray = mapper.readValue(url, NoFlyZone[].class);
        } catch (IOException e) {
            System.err.println("Error retrieving no-fly zones from REST API.");
            throw new RuntimeException(e);
        }

        // Convert the Array of NoFlyZones to a 2D array of LngLat
        LngLat[][] noFlyZoneArray = new LngLat[noFlyZoneObjectArray.length][];
        for (int i = 0; i < noFlyZoneObjectArray.length; i++) {
            List<LngLat> vertices = noFlyZoneObjectArray[i].getVertices();
            noFlyZoneArray[i] = vertices.toArray(new LngLat[0]);
        }
        return noFlyZoneArray;
    }

    /**
     * Accessor method for the central area border.
     *
     * @return The central area border represented by an array of LngLat points.
     */
    public LngLat[] getCentralAreaBorder() {
        return centralAreaBorder;
    }

    /**
     * Accessor method for the no-fly zones.
     *
     * @return An array of no-fly zones where each individual no-fly zone is an
     *         array of LngLat points.
     */
    public LngLat[][] getNoFlyZones() {
        return noFlyZones;
    }

}
