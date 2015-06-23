package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class ContinueOrStopExecutionEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Continue Or Stop Execution Event. This event allows the scheduler to know if the job associated to this event finished its execution or should continue.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor where the event should perform.
     */
    public ContinueOrStopExecutionEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    public String getName() {
        return "Continue Or Stop Execution Event";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void handle() {
        if(getScheduler().getExecutingJob(getProcessorId()) == getJob()) {
            long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
            getJob().execute(getTime() - lastExecutionTime);
            getScheduler().putLastJobExecution(getJob(), getTime());
            if(getJob().getRemainingTime() == 0){
                getScheduler().addEvent(new JobExecutionStopEvent(getScheduler(), getTime(), getJob(), getProcessorId()));
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
