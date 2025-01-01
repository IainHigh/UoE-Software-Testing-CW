package uk.ac.ed.inf.UnitTests.Output;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import Output.FileWriter;
import Output.FlightPathPoint;

public class FileWriterUnitTest {
        private final String date = "2025-01-01";
    private final String outputDirectory = "resultfiles/";

    @Test
    public void testWriteToDeliveriesJSON() throws IOException {
        FileWriter fileWriter = new FileWriter(date);
        String[] orderJson = {
            "{\"orderNumber\":\"ORD12345\",\"outcome\":\"DELIVERED\",\"costInPence\":1000}",
            "{\"orderNumber\":\"ORD67890\",\"outcome\":\"FAILED\",\"costInPence\":0}"
        };

        fileWriter.writeToDeliveriesJSON(orderJson);

        String filePath = outputDirectory + "deliveries-" + date + ".json";
        assertTrue("Deliveries JSON file should exist", Files.exists(Paths.get(filePath)));

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        assertEquals("[" + String.join(",", orderJson) + "]", content);

        // Cleanup
        Files.delete(Paths.get(filePath));
    }

    @Test
    public void testWriteToDroneGEOJSON() throws IOException {
        FileWriter fileWriter = new FileWriter(date);
        List<FlightPathPoint> flight = Arrays.asList(
            new FlightPathPoint("ORD12345", -3.186874, 55.944494, 45.0, -3.186000, 55.945000, 120),
            new FlightPathPoint("ORD67890", -3.186000, 55.945000, 90.0, -3.185000, 55.946000, 240)
        );
    
        fileWriter.writeToDroneGEOJSON(flight);
    
        String filePath = outputDirectory + "drone-" + date + ".geojson";
        assertTrue("Drone GEOJSON file should exist", Files.exists(Paths.get(filePath)));
    
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        System.out.println(content);
        assertTrue("Drone GEOJSON content should contain coordinates",
                   content.contains("[[-3.186874,55.944494],[-3.186,55.945],[-3.185,55.946]]"));
    //"coordinates": [[-3.186874,55.944494],[-3.186,55.945],[-3.185,55.946]
        // Cleanup
        //Files.delete(Paths.get(filePath));
    }
    

    @Test
    public void testWriteToFlightpathJSON() throws IOException {
        FileWriter fileWriter = new FileWriter(date);
        List<FlightPathPoint> flight = Arrays.asList(
            new FlightPathPoint("ORD12345", -3.186874, 55.944494, 45.0, -3.186000, 55.945000, 120),
            new FlightPathPoint("ORD67890", -3.186000, 55.945000, 90.0, -3.185000, 55.946000, 240)
        );

        fileWriter.writeToFlightpathJSON(flight);

        String filePath = outputDirectory + "flightpath-" + date + ".json";
        assertTrue("Flightpath JSON file should exist", Files.exists(Paths.get(filePath)));

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String expectedContent = "[" + flight.get(0).toJson() + "," + flight.get(1).toJson() + "]";
        assertEquals("Flightpath JSON content mismatch", expectedContent, content);

        // Cleanup
        Files.delete(Paths.get(filePath));
    }    
}
