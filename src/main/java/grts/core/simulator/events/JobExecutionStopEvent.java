package grts.core.simulator.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class JobExecutionStopEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Stop Job Execution Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor associated to the event.
     */
    public JobExecutionStopEvent(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time, job, processorId);
    }

    @Override
    public String getName() {
        return "Stop Job Execution Event";
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
    }

    @Override
    public JsonNode toLog() {
        JsonNode root = super.toLog();
        ObjectNode optionsNode = (ObjectNode) root.get("options");
        optionsNode.put("remaining time", getJob().getRemainingTime());
        return root;
    }

    @Override
    public String toString() {
        return "JobExecutionStopEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JobExecutionStopEvent)){
            return false;
        }
        JobExecutionStopEvent event = (JobExecutionStopEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
