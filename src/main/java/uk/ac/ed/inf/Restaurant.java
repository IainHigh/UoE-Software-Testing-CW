package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Restaurant {
    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("menu")
    private Menu[] menu;

    private int numberOfMovesFromAppletonTower;

    public Menu[] getMenu() {
        return menu;
    }

    public LngLat getLngLat() {
        return new LngLat(longitude, latitude);
    }

    public int getNumberOfMovesFromAppletonTower() {
        if (numberOfMovesFromAppletonTower == 0) {
            int numberOfMoves = this.getLngLat().numberOfMovesTo(Constants.APPLETON_TOWER);
            this.numberOfMovesFromAppletonTower = numberOfMoves;
            return numberOfMoves;
        }
        return numberOfMovesFromAppletonTower;
    }

    public static class Menu {
        public String name;
        public int priceInPence;
    }
}
