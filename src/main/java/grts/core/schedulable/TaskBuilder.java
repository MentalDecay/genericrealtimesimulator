package grts.core.schedulable;

import java.util.Map;
import java.util.function.Function;

public interface TaskBuilder {

    /**
     * Registers a new task in the TaskBuilder.
     * @param name The name of the task.
     * @param function The constructor of the task which returns a new Task and takes in parameters a map of String and Object.
     */
    void register(String name, Function<Map<String, Object>, ? extends Schedulable> function);
}
