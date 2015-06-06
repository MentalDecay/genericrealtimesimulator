package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class StopJobExecutionEvent extends AbstractEventOnJob implements IEvent {

    public StopJobExecutionEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void doEvent() {
        long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
        getJob().execute(getTime() - lastExecutionTime);
        getScheduler().stopJobExecution();
        if(getJob().getRemainingTime() == 0) {
            getScheduler().deleteActiveJob(getJob());
            getScheduler().addEvent(new ChooseJobEvent(getScheduler(), getTime()));
        }
        //getScheduler().addEvent(new CheckEndExecutionEventNotUsed(getScheduler(), getTime(), getJob()));

    }

    @Override
    public String toString() {
        return "StopJobExecutionEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StopJobExecutionEvent)){
            return false;
        }
        StopJobExecutionEvent event = (StopJobExecutionEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
