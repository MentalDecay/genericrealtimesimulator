package grts.core.json.parser.task;


import grts.core.schedulable.Schedulable;

public interface TaskParser {
    /**
     * Creates a new task according to the json.
     * @return a new task.
     */
    Schedulable newTask();
}
