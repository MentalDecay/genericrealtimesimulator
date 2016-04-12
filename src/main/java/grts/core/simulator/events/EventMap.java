package grts.core.simulator.events;

import java.util.HashMap;

/**
 * Created by jcharpen on 12/04/2016.
 */
public class EventMap {
    private static final HashMap<String, Class<?>> eventMap = new HashMap<>();
    private static final HashMap<String, Integer> priorityMap = new HashMap<>();

    /**
     * Put the class of an event in the map known by its String key
     * @param key a String
     * @param value the class of the event
     */
    public static void putEvent(String key, Class<?> value) {
        eventMap.put(key, value);
    }

    /**
     * Get the class of the event related to the String key
     * @param key a String
     * @return the class of the event
     */
    public static Class<?> getEvent(String key) {
        return eventMap.get(key);
    }

    /**
     * Put the priority in the map known by its String key
     * @param key a String
     * @param value an Integer
     */
    public static void putPriority(String key, int value) {
        priorityMap.put(key, value);
    }

    /**
     * Get the priority related to the String key
     * @param key a String
     * @return an Integer
     */
    public static int getPriority(String key) {
        return priorityMap.get(key);
    }
}
