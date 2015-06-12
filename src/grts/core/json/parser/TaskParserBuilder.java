package grts.core.json.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

public interface TaskParserBuilder {
    void register(String name, Function<JsonNode, ? extends TaskParser> function);
}
