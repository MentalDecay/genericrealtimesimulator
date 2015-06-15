package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class PreemptionEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Preemption Event.
     * @param scheduler The scheduler which created the event.
     * @param time the time of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor associated to the event.
     */
    public PreemptionEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    public void handle() {
        Job executingJob = getScheduler().getExecutingJob(getProcessorId());
        getScheduler().addEvent(new StopJobExecutionEvent(getScheduler(), getTime(), executingJob, getProcessorId()));
        getScheduler().executeJob(getJob(), getProcessorId());
        getScheduler().addEvent(new StartJobExecutionEvent(getScheduler(), getTime(), getJob(), getProcessorId()));
    }

    @Override
    public String toString() {
        return "PreemptionEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 8;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PreemptionEvent)){
            return false;
        }
        PreemptionEvent event = (PreemptionEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
