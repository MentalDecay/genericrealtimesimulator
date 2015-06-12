package grts.core.json.parser;


import grts.core.schedulable.Schedulable;

public interface TaskParser {
    Schedulable newTask();
}
