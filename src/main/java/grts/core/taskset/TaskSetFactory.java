package grts.core.taskset;

import grts.core.json.parser.SimulatorJacksonParser;


public interface TaskSetFactory {

    static TaskSet createTaskSetFromParser(SimulatorJacksonParser jacksonParser){
        return new TaskSet(jacksonParser.parseTasks());
    }
}
