package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JobExecutionStartEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Start Job Execution.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor associated to the event where the job should execute.
     */
    public JobExecutionStartEvent(Scheduler scheduler, long time, int priority, Job job, int processorId) {
        super(scheduler, time, priority, job, processorId);
    }

    @Override
    public String getName() {
        return "Start Job Execution Event";
    }

    @Override
    public void handle() {
        getScheduler().executeJob(getJob(), getProcessorId());
        getScheduler().putLastJobExecution(getJob(), getTime());
        Constructor<?> constructorContinueOrStop = null;
        try {
            constructorContinueOrStop = EventMap.getEvent("ContinueOrStopExecution").getConstructor(Scheduler.class, long.class, int.class, Job.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            getScheduler().addEvent((Event) constructorContinueOrStop.newInstance(getScheduler(), getTime() + getJob().getRemainingTime(), EventMap.getPriority("ContinueOrStopExecution"), getJob(), getProcessorId()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "JobExecutionStartEvent : " + getJob() + " on processor : " + getProcessorId() + " time : " + getTime();
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
