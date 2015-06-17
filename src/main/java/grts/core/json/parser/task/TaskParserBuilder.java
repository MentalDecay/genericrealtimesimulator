package grts.core.json.parser.task;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

public interface TaskParserBuilder {
    /**
     * The builder of task parsers. This method allows the user to add a new task parser to the factory.
     * @param name The name of the task parser to add.
     * @param function the function used to create a new task parser (usually the new).
     */
    void register(String name, Function<JsonNode, ? extends TaskParser> function);
}
