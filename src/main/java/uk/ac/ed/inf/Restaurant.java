package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Restaurant {
    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("menu")
    private Menu[] menu;
    public int numberOfMovesFromAppletonTower;

    public Menu[] getMenu() {
        return menu;
    }

    public LngLat getLngLat() {
        return new LngLat(longitude, latitude);
    }

    public static class Menu {
        public String name;
        public int priceInPence;
    }
}
