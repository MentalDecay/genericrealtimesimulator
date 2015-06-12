package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.ITask;

import java.util.List;

public class TaskSet extends AbstractTaskSet implements ITaskSet {


    public TaskSet(List<ITask> tasks) {
        super(tasks);
    }
}
