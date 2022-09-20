package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;

public class JSONRetriever {
    public void getJSON(String url) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Order> response = Arrays.asList(mapper.readValue(new URL(url),
                    Order[].class));
            for (Order i: response) {
                System.out.println(i.orderNo + " " + i.customer);
            }
        }
        catch (MalformedURLException e){
            System.err.println("The URL was malformed");
            System.exit(1);
        } catch (IOException e){
            System.err.println("There was an error reading the response");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void getNoFlyZones(String url) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            List<NoFlyZone> response = Arrays.asList(mapper.readValue(new URL(url),
                    NoFlyZone[].class));
            for (NoFlyZone i: response) {
                System.out.println(i.name);
                for (double[] j: i.coordinates) {
                    System.out.println(j[0] + " " + j[1]);
                }
            }
        }
        catch (MalformedURLException e){
            System.err.println("The URL was malformed");
            System.exit(1);
        } catch (IOException e){
            System.err.println("There was an error reading the response");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void getRestaurants(String url) {


        try {
            System.out.println(url);
            ObjectMapper mapper = new ObjectMapper();
            List<Restaurant> response = Arrays.asList(mapper.readValue(new URL(url),
                    Restaurant[].class));

            for (Restaurant i: response) {
                System.out.println(i.name);
                System.out.println(i.longitude + " " + i.latitude);
                System.out.println(i.menu);
            }
        }
        catch (MalformedURLException e){
            System.err.println("The URL was malformed");
            System.exit(1);
        } catch (IOException e){
            System.err.println("There was an error reading the response");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }



}
