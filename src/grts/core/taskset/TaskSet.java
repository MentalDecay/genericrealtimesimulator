package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;

import java.util.List;

public class TaskSet extends AbstractTaskSet implements ITaskSet {


    public TaskSet(List<AbstractRecurrentTask> tasks) {
        super(tasks);
    }
}