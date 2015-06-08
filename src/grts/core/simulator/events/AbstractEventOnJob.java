package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public abstract class AbstractEventOnJob  extends AbstractEvent implements IEvent {

    private final Job job;

    /**
     * Creates a new event linked to a job.
     * @param scheduler the scheduler linked to the event.
     * @param time the time of the event.
     * @param job the job of the event.
     */
    protected AbstractEventOnJob(Scheduler scheduler, long time, Job job) {
        super(scheduler, time);
        this.job = job;
    }

    /**
     * Get the job of the event.
     * @return the job of the event.
     */
    public Job getJob() {
        return job;
    }

}
