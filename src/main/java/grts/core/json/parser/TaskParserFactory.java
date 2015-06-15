package grts.core.json.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TaskParserFactory {

    /**
     * Creates a new task parser if this one is allowed according to the builder.
     * @param name The name of the task parser.
     * @param root The JsonNode root of the task which should be created.
     * @return A new task.
     */
    TaskParser create(String name, JsonNode root);

    /**
     * Initializes the factory with the tasks parsers to add.
     * @param consumer A consumer of TaskParserBuilder.
     * @return A new TaskParserFactory which contains every task parser added.
     */
    static TaskParserFactory create(Consumer<TaskParserBuilder> consumer) {
        HashMap<String, Function<JsonNode, ? extends TaskParser>> map = new HashMap<>();
        consumer.accept(map::put);
        return (name, parametersMap) -> map.getOrDefault(name,
                stringObjectHashMap -> {
                    throw new IllegalArgumentException("Unknown : " + name);
                })
                .apply(parametersMap);
    }
}
