package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public abstract class AbstractEventOnJob  extends AbstractEvent implements IEvent {

    private final Job job;

    protected AbstractEventOnJob(Scheduler scheduler, long time, Job job) {
        super(scheduler, time);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

}
