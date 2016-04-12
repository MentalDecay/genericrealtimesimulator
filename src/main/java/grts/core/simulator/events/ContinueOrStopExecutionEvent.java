package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ContinueOrStopExecutionEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Continue Or Stop Execution Event. This event allows the scheduler to know if the job associated to this event finished its execution or should continue.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     * @param job The job associated to the event.
     * @param processorId The id of the processor where the event should perform.
     */
    public ContinueOrStopExecutionEvent(Scheduler scheduler, long time, int priority, Job job, int processorId) {
        super(scheduler, time, priority, job, processorId);
    }

    @Override
    public String getName() {
        return "Continue Or Stop Execution Event";
    }

    @Override
    public void handle() {
        if(getScheduler().getExecutingJob(getProcessorId()) == getJob()) {
            long lastExecutionTime = getScheduler().getLastJobExecution().get(getJob());
            getJob().execute(getTime() - lastExecutionTime);
            System.out.println("execucuting : " + (getTime() - lastExecutionTime));
            System.out.println("remaining : " + getJob().getRemainingTime());
            getScheduler().putLastJobExecution(getJob(), getTime());
            if(getJob().getRemainingTime() == 0){
                Constructor<?> constructorJobStop = null;
                try {
                    constructorJobStop = EventMap.getEvent("JobExecutionStop").getConstructor(Scheduler.class, Long.class, Integer.class, Job.class, Integer.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                try {
                    getScheduler().addEvent((Event) constructorJobStop.newInstance(getScheduler(), getTime(), EventMap.getPriority("JobExecutionStop"), getJob(), getProcessorId()));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else{
                Constructor<?> constructorContinueOrStop = null;
                try {
                    constructorContinueOrStop = EventMap.getEvent("ContinueOrStopExecution").getConstructor(Scheduler.class, Long.class, Integer.class, Job.class, Integer.class);
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
