package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.ITask;

import java.util.List;

public interface ITaskSet {

    /**
     * Get the tasks attached to the task set.
     * @return a list of ITask
     */
    List<ITask> getTasks();

    /**
     * Get the hyper period of the task set
     * @return the hyper period of the task set
     */
    long getHyperPeriod();

}
