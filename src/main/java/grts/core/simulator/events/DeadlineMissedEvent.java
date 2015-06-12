package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public class DeadlineMissedEvent  extends AbstractEvent implements Event {

    public DeadlineMissedEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

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
    public boolean equals(Object obj) {
        if(!(obj instanceof DeadlineMissedEvent)){
            return false;
        }
        DeadlineMissedEvent event = (DeadlineMissedEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime();
    }
}
