package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DeadlineMissedEvent  extends AbstractEvent implements Event {

    /**
     * Creates a new Deadline Missed Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of th event.
     * @param priority the priority of the event.
     */
    public DeadlineMissedEvent(Scheduler scheduler, long time, int priority) {
        super(scheduler, time, priority);
    }

    @Override
    public void handle() {
        Constructor<?> constructorSimulationStop = null;
        try {
            constructorSimulationStop = EventMap.getEvent("SimulationStop").getConstructor(Scheduler.class, long.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            getScheduler().addEvent((Event) constructorSimulationStop.newInstance(getScheduler(), getTime(), EventMap.getPriority("SimulationStop")));
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
        return "DeadlineMissedEvent : time : " + getTime();
    }

    @Override
    public String getName() {
        return "Deadline Missed Event";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DeadlineMissedEvent)){
            return false;
        }
        DeadlineMissedEvent event = (DeadlineMissedEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime();
    }
}
