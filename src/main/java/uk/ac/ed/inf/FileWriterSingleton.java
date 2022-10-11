package uk.ac.ed.inf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileWriterSingleton {
    private static FileWriterSingleton instance;
    private static String date;
    private static final String outputDirectory = "outputFiles/";

    /**
     * @return The instance of the singleton.
     */
    public static FileWriterSingleton getInstance() {
        if (instance == null) {
            instance = new FileWriterSingleton();
        }
        return instance;
    }

    /**
     * Set the date for the output file.
     * @param date The date to set. Must be in the format YYYY-MM-DD.
     */
    public static void setDate(String date) {
        FileWriterSingleton.date = date;
    }

    /**
     * Write the order number, the order outcome and the cost to the deliveries file as an array of JSON objects.
     * @param orders The array of orders and their outcome to given on that day.
     */
    public void writeToDeliveriesJSON(Order[] orders) {
        String fileName = outputDirectory + "deliveries-" + date + ".json";
        prepareFile(fileName);

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("[");
            for (int i = 0; i < orders.length; i++) {
                fileWriter.write(orders[i].toJSON());
                if (i != orders.length - 1) fileWriter.write(",");
            }
            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the list of drone coordinates to the drone file as a LineString to view the path the drone took.
     * @param droneCoordinates The list of coordinates the drone took.
     */
    public void writeToDroneGEOJSON(List<double[]> droneCoordinates) {
        String fileName = outputDirectory + "drone-" + date + ".geojson";
        prepareFile(fileName);

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("{\"type\": \"Feature\",\"properties\": {}, \"geometry\": { \"type\": \"LineString\"," +
                    " " +
                    "\"coordinates\": [");
            for (int i = 0; i < droneCoordinates.size(); i++) {
                fileWriter.write(Arrays.toString(droneCoordinates.get(i)));
                if (i != droneCoordinates.size() - 1) fileWriter.write(",");
            }
            fileWriter.write("]}}");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the full flightpath of the drone to the flightpath output file.
     * @param flight The flightpath to write to the file.
     */
    public void writeToFlightpathJSON(List<FlightPathPoint> flight) {
        String fileName = outputDirectory + "flightpath-" + date + ".json";
        prepareFile(fileName);

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("[");
            for (int i = 0; i < flight.size(); i++) {
                fileWriter.write(flight.get(i).toJSON());
                if (i != flight.size() - 1) fileWriter.write(",");
            }
            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares the file for writing by ensuring it exists.
     * @param fileName The name of the file to prepare.
     */
    private void prepareFile(String fileName){
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
