package com.blue.mmccchunkloader.storage;

import java.util.HashMap;

public class ModConfiguration {
    //Default maximum chunks per player
    public static int defaultMax = 0;
    //Default state of offline persistence
    public static boolean defaultPersistance = false;
    //Default persistence timeout in seconds
    public static int persistanceTimeout = 120;
    //Default state for allowing loading in dimensions
    public static boolean defaultDimensionState = false;
    //Dimension loading whitelist
    public static HashMap<String, Boolean> dimensionWhitelist = new HashMap<>();
}
