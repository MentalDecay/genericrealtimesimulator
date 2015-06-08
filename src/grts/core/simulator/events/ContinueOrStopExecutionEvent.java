package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class ContinueOrStopExecutionEvent extends AbstractEventOnJob implements IEvent{

    public ContinueOrStopExecutionEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    protected int getPriority() {
        return 0;
    }

    @Override
    public void doEvent() {
        if(getScheduler().getExecutingJob(getProcessorId()) == getJob()) {
            long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
            getJob().execute(getTime() - lastExecutionTime);
            getScheduler().putLastJobExecution(getJob(), getTime());
            if(getJob().getRemainingTime() == 0){
                getScheduler().addEvent(new StopJobExecutionEvent(getScheduler(), getTime(), getJob(), getProcessorId()));
            }
            else{
                getScheduler().addEvent(new ContinueOrStopExecutionEvent(getScheduler(), getTime() + getJob().getRemainingTime(), getJob(), getProcessorId()));
            }
        }
    }

    @Override
    public String toString() {
        return "ContinueOrStopExecutionEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ContinueOrStopExecutionEvent)){
            return false;
        }
        ContinueOrStopExecutionEvent event = (ContinueOrStopExecutionEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
