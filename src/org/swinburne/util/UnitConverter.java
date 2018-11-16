package org.swinburne.util;

import org.swinburne.model.Node;

/**
 * Utility class that contains static methods to convert units and calculate geo-positional distance.
 */
public class UnitConverter {
    private static final float EARTH_RADIUS_METRE = 6371000;

    /**
     * Convert the unit kilometer per hour to meter per second.
     * @param kmh kilometer per hour input
     * @return meter per second conversion
     */
    public static double kmhToMs(double kmh) {
        double result = kmh * 1000 / 3600;
        return result;
    }

    /**
     * Calculate the distance between two coordinates of latitude and longitude using haversine forumla.
     * @param a first node that contains a coordinate
     * @param b second node that contains a coordinate
     * @return distance between two coordinates in meter
     */
    public static double geopositionDistance(Node a, Node b) {
        return geopositionDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }

    /**
     * Calculate the distance between two coordinates of latitude and longitude using haversine forumla.
     * @param aLat first coordinate latitude
     * @param aLon first coordinate longitude
     * @param bLat second coordinate latitude
     * @param bLon second coordinate longitude
     * @return distance between two coordinates in meter
     */
    public static double geopositionDistance(double aLat, double aLon, double bLat, double bLon) {
        double dLat = toRadian(bLat - aLat);
        double dLon = toRadian(bLon - aLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadian(aLat)) * Math.cos(toRadian(bLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METRE * c;
    }

    /**
     * Convert degree to radian
     * @param value degree
     * @return radian
     */
    private static double toRadian(double value) {
        return value * Math.PI / 180;
    }
}
