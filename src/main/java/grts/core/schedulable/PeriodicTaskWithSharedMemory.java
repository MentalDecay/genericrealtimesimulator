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

    @Override
    public Schedulable copy() {
//        TODO Complete copy
        return new PeriodicTaskWithSharedMemory(getPeriod(), getWcet(), getDeadline(), getOffset(), getName());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
//        TODO Add the shared memory to the hash code.
        return hash;
    }
}
