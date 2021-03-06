package com.weareforge.qms.helpers;

/**
 * Created by Admin on 2/10/2016.
 */
public enum WifiSignalLevel {
    NO_SIGNAL(0, "no signal"),
    POOR(1, "poor"),
    FAIR(2, "fair"),
    GOOD(3, "good"),
    EXCELLENT(4, "excellent");

    public final int level;
    public final String description;

    WifiSignalLevel(final int level, final String description) {
        this.level = level;
        this.description = description;
    }

    public static int getMaxLevel() {
        return EXCELLENT.level;
    }

    /**
     * Gets WifiSignalLevel enum basing on integer value
     * @param level as an integer
     * @return WifiSignalLevel enum
     */
    public static WifiSignalLevel fromLevel(final int level) {
        switch (level) {
            case 0:
                return NO_SIGNAL;
            case 1:
                return POOR;
            case 2:
                return FAIR;
            case 3:
                return GOOD;
            case 4:
                return EXCELLENT;
            default:
                return NO_SIGNAL;
        }
    }

    @Override public String toString() {
        return "WifiSignalLevel{" + "level=" + level + ", description='" + description + '\'' + '}';
    }
}