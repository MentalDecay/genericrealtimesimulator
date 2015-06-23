package grts.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grts.core.simulator.events.Event;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class EventLogger {

    private final ObjectMapper mapper = new ObjectMapper();
    private final OutputStream outputStream;
    private final ArrayNode arrayNode = mapper.createArrayNode();
    private final List<String> eventsList;

    /**
     * Creates a new Event Logger.
     * @param folderPath The path to the folder where the file will be created.
     * @param eventsList The list of names of events to log.
     * @throws IOException if the file cannot be created.
     */
    public EventLogger(String folderPath, List<String> eventsList) throws IOException {
        this.eventsList = eventsList;
        outputStream = Files.newOutputStream(Paths.get(folderPath, "EventLog").toAbsolutePath(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    /**
     * Logs an event. If the event is in the list of events to log, the result is saved in a json object.
     * @param event The event to log.
     */
    public void log(Event event){
        if(eventsList.contains(event.getName())) {
            arrayNode.add(event.toLog());
        }
    }

    /**
     * Writes the json tree in the file.
     */
    public void writeJson(){
        ObjectNode root = mapper.createObjectNode();
        root.set("events", arrayNode);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, root);
        } catch (IOException e) {
            System.err.println("Can't write the json output");
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Error while closing the output stream");
        }
    }
}
