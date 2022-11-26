package Output;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A class for writing the output to the required files.
 * Writes the orderNo, outcome and costInPence for every order to deliveries-YYYY-MM-DD.json
 * Writes the flight path information to flightpath-YYYY-MM-DD.json
 * Writes the visited coordinates to drone-YYYY-MM-DD.geojson.
 */
public class FileWriter {
    private final String DATE;
    private final String OUTPUT_DIRECTORY = "outputFiles/";

    /**
     * Constructor for the FileWriter class.
     *
     * @param date The date of the orders.
     */
    public FileWriter(String date) {
        this.DATE = date;
    }

    /**
     * Writes the orderNo, outcome and costInPence for every order to deliveries-YYYY-MM-DD.json.
     *
     * @param orderJson The String representation of the order in JSON format.
     */
    public void writeToDeliveriesJSON(String[] orderJson) {
        String fileName = OUTPUT_DIRECTORY + "deliveries-" + DATE + ".json";
        prepareFile(fileName);

        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(fileName);
            fileWriter.write("[");
            for (int i = 0; i < orderJson.length; i++) {
                fileWriter.write(orderJson[i]);
                if (i != orderJson.length - 1) fileWriter.write(",");
            }
            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to deliveries json");
            e.printStackTrace();
        }
    }

    /**
     * Write the list of drone coordinates to the drone file as a LineString to view the path the drone took.
     *
     * @param flight The flightpath - used to get the coordinates to write.
     */
    public void writeToDroneGEOJSON(List<FlightPathPoint> flight) {
        double[] startingCoordinates = flight.get(0).getStartingCoordinates();
        List<double[]> droneCoordinates = flight.stream().map(FlightPathPoint::getDestinationCoordinates).toList();

        String fileName = OUTPUT_DIRECTORY + "drone-" + DATE + ".geojson";
        prepareFile(fileName);

        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(fileName);
            fileWriter.write("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"LineString\"," + " " + "\"coordinates\": [");
            fileWriter.write(Arrays.toString(startingCoordinates));
            fileWriter.write(",");
            for (int i = 0; i < droneCoordinates.size(); i++) {
                fileWriter.write(Arrays.toString(droneCoordinates.get(i)));
                if (i != droneCoordinates.size() - 1) fileWriter.write(",");
            }
            fileWriter.write("]}}");
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to drone geojson");
            e.printStackTrace();
        }
    }

    /**
     * Write the full flightpath of the drone to the flightpath output file.
     *
     * @param flight The flightpath to write to the file.
     */
    public void writeToFlightpathJSON(List<FlightPathPoint> flight) {
        String fileName = OUTPUT_DIRECTORY + "flightpath-" + DATE + ".json";
        prepareFile(fileName);

        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(fileName);
            fileWriter.write("[");
            for (int i = 0; i < flight.size(); i++) {
                fileWriter.write(flight.get(i).toJSON());
                if (i != flight.size() - 1) fileWriter.write(",");
            }
            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to flightpath json");
            e.printStackTrace();
        }
    }

    /**
     * Prepares the file for writing by ensuring it exists.
     *
     * @param fileName The name of the file to prepare.
     */
    private void prepareFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating file" + fileName);
            e.printStackTrace();
        }
    }
}
