package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class StopJobExecutionEvent extends AbstractEventOnJob implements Event {

    public StopJobExecutionEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    public void handle() {
        long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
        getJob().execute(getTime() - lastExecutionTime);
        getScheduler().stopJobExecution(getJob(), getProcessorId());
        if(getJob().getRemainingTime() == 0) {
            getScheduler().deleteActiveJob(getJob());
            getScheduler().addEvent(new ChooseJobEvent(getScheduler(), getTime()));
        }
        //getScheduler().addEvent(new CheckEndExecutionEventNotUsed(getScheduler(), getTime(), getJob()));

    }

    @Override
    public String toString() {
        return "StopJobExecutionEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
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
