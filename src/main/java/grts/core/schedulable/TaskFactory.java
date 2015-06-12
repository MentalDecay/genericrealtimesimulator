package grts.core.schedulable;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TaskFactory {

    Schedulable create(String name, Map<String, Object> parametersMap);

    static TaskFactory create(Consumer<TaskBuilder> consumer) {
        HashMap<String, Function<Map<String, Object>, ? extends Schedulable>> map = new HashMap<>();
        consumer.accept(map::put);
        return (name, parametersMap) -> map.getOrDefault(name,
                stringObjectHashMap -> {
                    throw new IllegalArgumentException("Unknown : " + name);
                })
                .apply(parametersMap);
    }
}
