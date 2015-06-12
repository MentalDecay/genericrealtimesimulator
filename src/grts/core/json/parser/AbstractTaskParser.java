package grts.core.json.parser;

import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.SporadicTask;
import grts.core.schedulable.TaskBuilder;
import grts.core.schedulable.TaskFactory;

public abstract class AbstractTaskParser implements TaskParser{
    protected final TaskFactory factory = TaskFactory.create(taskBuilder -> {
        taskBuilder.register("PeriodicTask", PeriodicTask::new);
        taskBuilder.register("SporadicTask", SporadicTask::new);
    });
}
