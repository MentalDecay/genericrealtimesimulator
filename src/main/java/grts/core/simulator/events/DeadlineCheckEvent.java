package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Tristan Fautrel
 */
public class DeadlineCheckEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Check Deadline Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     * @param job The job associated to the event.
     */
    public DeadlineCheckEvent(Scheduler scheduler, long time, int priority, Job job) {
        super(scheduler, time, priority, job);
    }

    @Override
    public String getName() {
        return "Check Deadline Event";
    }

    @Override
    public void handle() {
        if(getJob().getRemainingTime() > 0 ){
            System.out.println("fail on : " + getJob());
            System.out.println("remaining : " + getJob().getRemainingTime());
            Constructor<?> constructorDeadlineMissed = null;
            try {
                constructorDeadlineMissed = EventMap.getEvent("DeadlineMissed").getConstructor(Scheduler.class, long.class, int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                getScheduler().addEvent((Event) constructorDeadlineMissed.newInstance(getScheduler(), getTime(), EventMap.getPriority("DeadlineMissed")));
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
    public String toString() {
        return "DeadlineCheckEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DeadlineCheckEvent)){
            return false;
        }
        DeadlineCheckEvent event = (DeadlineCheckEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }
}
