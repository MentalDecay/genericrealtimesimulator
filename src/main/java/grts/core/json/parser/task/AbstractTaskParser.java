package grts.core.json.parser.task;

import grts.core.schedulable.*;

public abstract class AbstractTaskParser implements TaskParser{
    protected final TaskFactory factory = TaskFactory.create(taskBuilder -> {
        taskBuilder.register("PeriodicTask", PeriodicTask::new);
        taskBuilder.register("SporadicTask", SporadicTask::new);
        taskBuilder.register("PeriodicTaskEnergyAware", PeriodicTaskEnergyAware::new);
        taskBuilder.register("PeriodicTaskMemoryAware", PeriodicTaskMemoryAware::new);
        taskBuilder.register("PeriodicTaskWithShareMemory", PeriodicTaskWithSharedMemory::new);
    });
}
