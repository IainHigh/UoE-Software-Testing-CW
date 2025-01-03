package PizzaDronz;

import RouteCalculation.LngLat;

/**
 * A class for storing all the constants used in the project.
 * This is to avoid magic numbers and strings.
 */
public final class Constants {
    static final LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);
    static final int MAX_MOVES = 2000;
    // The following constants are used for the REST endpoint URL.
    static final String TEST_URL_SLUG = "/actuator/health/livenessState";
    static final String CENTRAL_AREA_URL_SLUG = "/centralarea";
    static final String NO_FLY_ZONES_URL_SLUG = "/noflyzones";
    static final String RESTAURANTS_URL_SLUG = "/restaurants";
    static final String ORDERS_WITH_DATE_URL_SLUG = "/orders";
}
