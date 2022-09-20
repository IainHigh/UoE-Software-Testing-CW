package uk.ac.ed.inf;

import uk.ac.ed.inf.JSONRetriever;

public class TestClass {
    public static void main(String[] args) {
        JSONRetriever retriever = new JSONRetriever();
        retriever.getJSON("https://ilp-rest.azurewebsites.net/orders");
        retriever.getNoFlyZones("https://ilp-rest.azurewebsites.net/noFlyZones");
        retriever.getRestaurants("https://ilp-rest.azurewebsites.net/restaurants");
    }
}
