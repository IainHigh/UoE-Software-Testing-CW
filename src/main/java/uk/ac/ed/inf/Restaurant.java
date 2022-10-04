package uk.ac.ed.inf;

import java.net.URL;

public class Restaurant {
    public String name;
    public double longitude;
    public double latitude;
    public Menu[] menu;

    public Menu[] getMenu() {
        return menu;
    }

    public static Restaurant[] getRestaurantsFromRestServer(URL serverBaseAddress) {
        var retriever = new JSONRetriever();
        return retriever.getRestaurants(serverBaseAddress);
    }
}
