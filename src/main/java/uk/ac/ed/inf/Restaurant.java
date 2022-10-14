package uk.ac.ed.inf;

import IO.JSONRetriever;

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

    public static class Menu {
        public String name;
        public int priceInPence;
    }
}
