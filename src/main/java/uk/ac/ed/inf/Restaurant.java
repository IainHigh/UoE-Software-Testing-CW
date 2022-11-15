package uk.ac.ed.inf;

public class Restaurant {
    public String name;
    public double longitude;
    public double latitude;
    public Menu[] menu;

    public Menu[] getMenu() {
        return menu;
    }

    public static class Menu {
        public String name;
        public int priceInPence;
    }
}
