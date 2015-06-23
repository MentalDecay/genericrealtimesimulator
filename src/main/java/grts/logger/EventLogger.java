/* Copyright (c) 2015, Tristan Fautrel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import java.util.TreeSet;

/**
 * This class represents a logger for simulation events.
 * @author Tristan Fautrel
 */
public class EventLogger {

    private final ObjectMapper mapper = new ObjectMapper();
    private final OutputStream outputStream;
    private final ArrayNode arrayNode = mapper.createArrayNode();
    private final TreeSet<Class<? extends Event>> loggables = new TreeSet<>((c1, c2) -> c1.hashCode() - c2.hashCode());

    /**
     * Creates a new Event Logger.
     * @param folderPath The path to the folder where the file will be created.
     * @param loggables The set of event classes to log.
     * @throws IOException if the file cannot be created.
     */
    public EventLogger(String folderPath, Class<? extends Event>... loggables) throws IOException {
        outputStream = Files.newOutputStream(Paths.get(folderPath, "EventLog").toAbsolutePath(),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        for (Class<? extends Event> loggable : loggables) {
            this.loggables.add(loggable);
        }
    }

    /**
     * Logs an event. If the event is in the list of events to log, the result is saved in a json object.
     * @param event The event to log.
     */
    public void log(Event event){
        if(loggables.contains(event.getClass())) {
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