package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public class StopSimulationEvent extends AbstractEvent implements Event {


    /**
     * Creates a new Stop Simulation Event. This event stops the simulation.
     * @param scheduler The scheduler which created this event.
     * @param time The time of the event.
     */
    public StopSimulationEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

    public void handle() {
        getScheduler().endSimulation();
    }

    @Override
    public String toString() {
        return "StopSimulationEvent : time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StopSimulationEvent)){
            return false;
        }
        StopSimulationEvent event = (StopSimulationEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime();
    }

}
