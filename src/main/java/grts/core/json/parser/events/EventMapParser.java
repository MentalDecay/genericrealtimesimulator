package grts.core.json.parser.events;

import com.fasterxml.jackson.databind.JsonNode;
import grts.core.simulator.events.EventMap;

import java.util.Objects;

/**
 * Created by jcharpen on 12/04/2016.
 */
public class EventMapParser {
    private final JsonNode root;

    private EventMapParser(JsonNode root) {
        this.root = root;
    }

    /**
     * Create the parser for EventMap
     * @param root the root of the json file
     * @return an EventMapParser
     */
    public static EventMapParser create(JsonNode root) {
        if(root == null) {
            throw new IllegalArgumentException("Root is null");
        }
        JsonNode events = root.get("events");
        if(events == null || !events.isArray()) {
            throw new IllegalArgumentException("The json file is ill-formed : there is no array of events");
        }
        return new EventMapParser(events);
    }

    /**
     * Parse the events from the json file and put the information in the static map of EventMap
     * @throws ClassNotFoundException if the class is not found
     */
    public void parseEvents() throws ClassNotFoundException {
        for(JsonNode event : root) {
            JsonNode nameNode = event.get("name");
            if(nameNode == null) {
                throw new IllegalStateException("The json file is ill-formed : there is no name");
            }
            JsonNode classNode = event.get("class");
            if(classNode == null) {
                throw new IllegalStateException("The json file is ill-formed : there is no class name");
            }
            JsonNode priorityNode = event.get("priority");
            if(priorityNode == null) {
                throw new IllegalStateException("The json file is ill-formed : there is no priority");
            }
            String name = nameNode.textValue();
            String className = classNode.textValue();
            Class<?> eventClass = Class.forName(className);
            int priority = priorityNode.intValue();
            EventMap.putEvent(name, eventClass);
            EventMap.putPriority(name, priority);
        }
    }
}
