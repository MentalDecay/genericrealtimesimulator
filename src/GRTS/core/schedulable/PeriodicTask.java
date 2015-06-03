package GRTS.core.schedulable;

public class PeriodicTask extends AbstractRecurrentTask implements ITask {


    public PeriodicTask(long period, long wcet, long deadline, long offset, String name) {
        super(period, wcet, deadline, offset, name);
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
}
