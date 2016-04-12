package grts.core.simulator.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JobExecutionStopEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Stop Job Execution Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor associated to the event.
     */
    public JobExecutionStopEvent(Scheduler scheduler, long time, int priority, Job job, int processorId) {
        super(scheduler, time, priority, job, processorId);
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
            Constructor<?> constructorChoose = null;
            try {
                constructorChoose = EventMap.getEvent("ChooseJob").getConstructor(Scheduler.class, Long.class, Integer.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                getScheduler().addEvent((Event) constructorChoose.newInstance(getScheduler(), getTime(), EventMap.getPriority("ChooseJob")));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
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
