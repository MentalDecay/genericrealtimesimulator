package grts.core.schedulable;

import java.util.Map;
import java.util.function.Function;

public interface TaskBuilder {
    void register(String name, Function<Map<String, Object>, ? extends ITask> function);
}
