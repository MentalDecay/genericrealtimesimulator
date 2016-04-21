package grts.core.schedulable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PeriodicTask extends AbstractRecurrentTask implements Schedulable {

    private Job realNextJob;

    /**
     * Creates a new periodic task.
     * @param period The period of the task.
     * @param wcet The Worst Case Execution Time of the task.
     * @param deadline The deadline of the task.
     * @param offset The offset of the task.
     * @param name The name of the task.
     */
    public PeriodicTask(long period, long wcet, long deadline, long offset, String name) {
        super(period, wcet, deadline, offset, name);
        realNextJob = createJob(offset, offset + deadline, wcet);
    }

    /**
     * Creates a new periodic task.
     * @param map a map of String and Object which contains the minimumInterArrivalTime (long), the wcet (long), the deadline (long), the offset (long)
     * and the name (String).
     */
    public PeriodicTask(Map<String, Object> map){
        super(map);
        realNextJob = createJob(getOffset(), getOffset() + getDeadline(), getWcet());
    }

    /**
     * The inter arrival time of a simple task. It's always the period.
     * @return the inter arrival time of a simple task
     */
    @Override
    protected long getNextInterArrivalTime() {
        return getMinimumInterArrivalTime();
    }

    /**
     * Get the minimum inter arrival time, namely the period for a simple task.
     * @return the period of the task
     */
    public long getPeriod() {
        return getMinimumInterArrivalTime();
    }

    @Override
    public List<Job> getRealNextJob(long time) {
        List<Job> ret = new LinkedList<>();
        if(realNextJob != null && time < realNextJob.getActivationTime()){
            ret.add(realNextJob);
            return ret;
        }
        else{
            assert realNextJob != null;
            long activationTime = realNextJob.getActivationTime() + getNextInterArrivalTime();
            realNextJob = createJob(activationTime, activationTime + getDeadline(), getWcet());
            ret.add(realNextJob);
            return ret;
        }
    }

    @Override
    public Schedulable copy() {
        return new PeriodicTask(getPeriod(), getWcet(), getDeadline(), getOffset(), getName());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PeriodicTask)){
            return false;
        }
        PeriodicTask task = (PeriodicTask) obj;
        return getMinimumInterArrivalTime() == task.getMinimumInterArrivalTime() &&
                getWcet()  == task.getWcet() &&
                getDeadline() == task.getDeadline() &&
                getOffset() == task.getOffset() &&
                getName().equals(task.getName());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
        return hash;
    }

    @Override
    public String toString() {
//        long period, long wcet, long deadline, long offset, String name
        return "Periodic Task : \n" +
                "period : " + getPeriod() + "\n" +
                "wcet : " + getWcet() + "\n" +
                "deadline : " + getDeadline() + "\n" +
                "offset : " + getOffset() + "\n" +
                "name : " + getName();
    }
}
