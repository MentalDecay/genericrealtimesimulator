package grts.core.schedulable;

import java.util.Map;

public class PeriodicTaskMemoryAware extends PeriodicTask{

    private final long memory;

    /**
     * Creates a new PeriodicTask which is aware of the memory element.
     * @param map The map of String and Object which contains the minimumInterArrivalTime (long), the wcet (long), the deadline (long), the offset (long),
     * the name (String) and the memory (long).
     */
    public PeriodicTaskMemoryAware(Map<String, Object> map) {
        super(map);
        if(map.get("memory") == null){
            throw new IllegalArgumentException("No memory in the map.");
        }
        memory = (long) map.get("memory");
    }

    /**
     * Creates a new PeriodicTask which is aware of the memory element.
     * @param period The period of the task.
     * @param wcet The Worst Case Execution Time of the task.
     * @param deadline The deadline of the task.
     * @param offset The offset of the task.
     * @param name The name of the task.
     * @param memory The memory cost of the task.
     */
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

    @Override
    public Schedulable copy() {
        return new PeriodicTaskMemoryAware(getPeriod(), getWcet(), getDeadline(), getOffset(), getName(), memory);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
        hash = hash * 31 + Float.floatToIntBits(memory);
        return hash;
    }
}
