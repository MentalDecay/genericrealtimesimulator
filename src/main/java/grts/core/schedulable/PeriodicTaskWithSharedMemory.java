package grts.core.schedulable;

import java.util.Map;

public class PeriodicTaskWithSharedMemory extends PeriodicTask {
//    TODO add resources in the constructor.
//    TODO from : time to : time resource : resource.
    public PeriodicTaskWithSharedMemory(Map<String, Object> map) {
        super(map);
    }

    public PeriodicTaskWithSharedMemory(long period, long wcet, long deadline, long offset, String name) {
        super(period, wcet, deadline, offset, name);
    }
}
