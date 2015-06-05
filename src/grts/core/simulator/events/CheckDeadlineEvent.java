package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class CheckDeadlineEvent extends AbstractEventOnJob implements IEvent {

    public CheckDeadlineEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void doEvent() {
        if(getJob().getRemainingTime() > 0 ){
            getScheduler().addEvent(new DeadlineMissedEvent(getScheduler(), getTime()));
        }
        else{
            //Do nothing
        }
    }

    @Override
    public String toString() {
        return "CheckDeadlineEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 5;
    }
}