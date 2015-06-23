package grts.core.schedulable;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TaskFactory {

    /**
     * Creates a new task.
     * @param name The name of the task.
     * @param parametersMap The map of String, Object to create the new task.
     * @return A new task.
     */
    Schedulable create(String name, Map<String, Object> parametersMap);

    /**
     * Initializes the factory.
     * @param consumer A consumer of TaskBuilder to initialize the factory.
     * @return A new Task Factory.
     */
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