package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class JSONRetriever {
    public List<Order> getOrders(URL url) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Order> response = Arrays.asList(mapper.readValue(url, Order[].class));
            return response;
        }
        catch (MalformedURLException e){
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public List<NoFlyZone> getNoFlyZones(URL url) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<NoFlyZone> response = Arrays.asList(mapper.readValue(url, NoFlyZone[].class));
            return response;
        }
        catch (MalformedURLException e){
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public CentralAreaPoint[] getCentralArea(URL url) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<CentralAreaPoint> response = Arrays.asList(mapper.readValue(url, CentralAreaPoint[].class));
            return response.toArray(new CentralAreaPoint[response.size()]);
        }
        catch (MalformedURLException e){
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Restaurant[] getRestaurants(URL url) {

        try {
            System.out.println(url);
            ObjectMapper mapper = new ObjectMapper();
            List<Restaurant> response = Arrays.asList(mapper.readValue(url, Restaurant[].class));

            return response.toArray(new Restaurant[response.size()]);
        }
        catch (MalformedURLException e){
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
