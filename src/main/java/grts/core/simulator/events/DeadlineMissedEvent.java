package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public class DeadlineMissedEvent  extends AbstractEvent implements Event {

    /**
     * Creates a new Deadline Missed Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of th event.
     */
    public DeadlineMissedEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

    @Override
    public void handle() {
//        TODO logs
        getScheduler().addEvent(new StopSimulationEvent(getScheduler(), getTime()));
    }

    @Override
    public String toString() {
        return "DeadlineMissedEvent : time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 4;
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
