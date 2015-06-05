package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class ContinueOrStopExecutionEvent extends AbstractEventOnJob implements IEvent{

    public ContinueOrStopExecutionEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    protected int getPriority() {
        return 0;
    }

    @Override
    public void doEvent() {
        if(getScheduler().getExecutingJob() == getJob()) {
            long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
            getJob().execute(getTime() - lastExecutionTime);
            getScheduler().putLastJobExecution(getJob(), getTime());
            if(getJob().getRemainingTime() == 0){
                getScheduler().addEvent(new StopJobExecutionEvent(getScheduler(), getTime(), getJob()));
            }
            else{
                getScheduler().addEvent(new ContinueOrStopExecutionEvent(getScheduler(), getTime() + getJob().getRemainingTime(), getJob()));
            }
        }
    }

    @Override
    public String toString() {
        return "ContinueOrStopExecutionEvent : " + getJob() + " time : " + getTime();
    }
}
