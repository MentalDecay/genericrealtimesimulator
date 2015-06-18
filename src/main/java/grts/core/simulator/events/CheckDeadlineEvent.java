package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class CheckDeadlineEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Check Deadline Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param job The job associated to the event.
     */
    public CheckDeadlineEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public String getName() {
        return "Check Deadline Event";
    }

    @Override
    public void handle() {
        if(getJob().getRemainingTime() > 0 ){
            getScheduler().addEvent(new DeadlineMissedEvent(getScheduler(), getTime()));
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CheckDeadlineEvent)){
            return false;
        }
        CheckDeadlineEvent event = (CheckDeadlineEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
