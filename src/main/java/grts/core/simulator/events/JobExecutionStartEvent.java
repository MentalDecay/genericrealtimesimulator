package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class JobExecutionStartEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Start Job Execution.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor associated to the event where the job should execute.
     */
    public JobExecutionStartEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    public String getName() {
        return "Start Job Execution Event";
    }

    @Override
    public void handle() {
        getScheduler().executeJob(getJob(), getProcessorId());
        getScheduler().putLastJobExecution(getJob(), getTime());
        getScheduler().addEvent(new ContinueOrStopExecutionEvent(getScheduler(), getTime() + getJob().getRemainingTime(), getJob(), getProcessorId()));

    }

    @Override
    public String toString() {
        return "JobExecutionStartEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
    }

    @Override
    public int getPriority() {
        return 9;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JobExecutionStartEvent)){
            return false;
        }
        JobExecutionStartEvent event = (JobExecutionStartEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}