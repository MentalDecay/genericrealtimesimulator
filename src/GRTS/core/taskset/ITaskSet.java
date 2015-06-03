package GRTS.core.taskset;

import GRTS.core.schedulable.AbstractRecurrentTask;

import java.util.List;

public interface ITaskSet {

    /**
     * Get the tasks attached to the task set.
     * @return a list of ITask
     */
    List<AbstractRecurrentTask> getRecurrentTasks();

    /**
     * Get the hyper period of the task set
     * @return the hyper period of the task set
     */
    long getHyperPeriod();

}
