package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class CheckEndExecutionEventNotUsed extends AbstractEventOnJob implements IEvent {

    protected CheckEndExecutionEventNotUsed(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void doEvent() {
        if(getJob().getRemainingTime() == 0) {
            getScheduler().deleteActiveJob(getJob());
            getScheduler().stopJobExecution();
            getScheduler().addEvent(new ChooseJobEvent(getScheduler(), getTime()));
        }
        else{
            //DO NOTHING
        }
    }

    @Override
    public String toString() {
        return "CheckEndExecutionEventNotUsed : " + getJob() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 3;
    }
}
