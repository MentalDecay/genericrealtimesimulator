package grts.core.schedulable;

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
     * @param map a map of String and Object which contains the minimumInterArrivalTime (long), the wcet (long), the deadline (long), the offset (long) and the name (String).
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
    public Job getRealNextJob(long time) {
        if(realNextJob != null && time < realNextJob.getActivationTime()){
            return realNextJob;
        }
        else{
            assert realNextJob != null;
            long activationTime = realNextJob.getActivationTime() + getNextInterArrivalTime();
            realNextJob = createJob(activationTime, activationTime + getDeadline(), getWcet());
            return realNextJob;
        }
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

}
