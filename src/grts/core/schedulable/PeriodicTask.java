package grts.core.schedulable;

public class PeriodicTask extends AbstractRecurrentTask implements ITask {

    private long lastJob = 0;
    private Job realNextJob;

    public PeriodicTask(long period, long wcet, long deadline, long offset, String name) {
        super(period, wcet, deadline, offset, name);
        realNextJob = createJob(offset, offset + deadline, wcet);
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
            long activationTime = realNextJob.getActivationTime() + getNextInterArrivalTime();
            realNextJob = createJob(activationTime, activationTime + getDeadline(), getWcet());
            return realNextJob;
        }
    }
}
