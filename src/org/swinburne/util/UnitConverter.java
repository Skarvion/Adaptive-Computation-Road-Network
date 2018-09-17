package org.swinburne.util;

public class UnitConverter {
    private static final float EARTH_RADIUS_METRE = 6371000;

    public static double kmhToMs(double kmh) {
        double result = kmh * 1000 / 3600;
        return result;
    }

    // Harvesine formula
    public static double geopositionDistance(double aLat, double aLon, double bLat, double bLon) {
        double dLat = toRadian(bLat - aLat);
        double dLon = toRadian(bLon - aLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadian(aLat)) * Math.cos(toRadian(bLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METRE * c;
    }

    private static double toRadian(double value) {
        return value * Math.PI / 180;
    }
}
