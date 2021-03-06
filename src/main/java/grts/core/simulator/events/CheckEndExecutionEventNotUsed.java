package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class CheckEndExecutionEventNotUsed extends AbstractEventOnJob implements Event {

    protected CheckEndExecutionEventNotUsed(Scheduler scheduler, long time, int priority, Job job) {
        super(scheduler, time, priority, job);
    }

    @Override
    public String getName() {
        return "Check End Execution Event";
    }

    @Override
    public void handle() {
        /*LEGACY
        if(getJob().getRemainingTime() == 0) {
            getScheduler().deleteActiveJob(getJob());
            getScheduler().addEvent(new ChooseJobEvent(getScheduler(), getTime()));
        }*/
    }

    @Override
    public String toString() {
        return "CheckEndExecutionEventNotUsed : " + getJob() + " time : " + getTime();
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CheckEndExecutionEventNotUsed)){
            return false;
        }
        CheckEndExecutionEventNotUsed event = (CheckEndExecutionEventNotUsed) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
