package grts.core.json.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TaskParserFactory {
    TaskParser create(String name, JsonNode root);

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
