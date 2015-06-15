package grts.core.json.parser;


import grts.core.schedulable.Schedulable;

public interface TaskParser {
    /**
     * Creates a new task according to the json.
     * @return a new task.
     */
    Schedulable newTask();
}
