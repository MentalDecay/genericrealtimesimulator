package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class StartJobExecutionEvent extends AbstractEventOnJob implements IEvent {

    public StartJobExecutionEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void doEvent() {
        getScheduler().executeJob(getJob());
        getScheduler().putLastJobExecution(getJob(), getTime());
        getScheduler().addEvent(new ContinueOrStopExecutionEvent(getScheduler(), getTime() + getJob().getRemainingTime(), getJob()));

    }

    @Override
    public String toString() {
        return "StartJobExecutionEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 9;
    }
}
