package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class PreemptionEvent extends AbstractEventOnJob implements IEvent {

    public PreemptionEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void doEvent() {
        Job executingJob = getScheduler().getExecutingJob();
        getScheduler().addEvent(new StopJobExecutionEvent(getScheduler(), getTime(), executingJob));
        getScheduler().executeJob(getJob());
        getScheduler().addEvent(new StartJobExecutionEvent(getScheduler(), getTime(), getJob()));
    }

    @Override
    public String toString() {
        return "PreemptionEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 8;
    }
}
