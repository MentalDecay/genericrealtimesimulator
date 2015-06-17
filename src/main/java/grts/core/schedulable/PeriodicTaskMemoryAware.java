package grts.core.schedulable;

import java.util.Map;

public class PeriodicTaskMemoryAware extends PeriodicTask{

    private final long memory;

    public PeriodicTaskMemoryAware(Map<String, Object> map) {
        super(map);
        if(map.get("memory") == null){
            throw new IllegalArgumentException("No memory in the map.");
        }
        memory = (long) map.get("memory");
    }

    public PeriodicTaskMemoryAware(long period, long wcet, long deadline, long offset, String name, long memory) {
        super(period, wcet, deadline, offset, name);
        this.memory = memory;
    }

    /**
     * Get the memory needs of the task.
     * @return The memory needs of the task.
     */
    public long getMemory() {
        return memory;
    }
}
