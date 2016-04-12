package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public class SimulationStopEvent extends AbstractEvent implements Event {


    /**
     * Creates a new Stop Simulation Event. This event stops the simulation.
     * @param scheduler The scheduler which created this event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     */
    public SimulationStopEvent(Scheduler scheduler, long time, int priority) {
        super(scheduler, time, priority);
    }

    public void handle() {
        getScheduler().endSimulation();
    }

    @Override
    public String toString() {
        return "SimulationStopEvent : time : " + getTime();
    }

    @Override
    public String getName() {
        return "Stop Simulation Event";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SimulationStopEvent)){
            return false;
        }
        SimulationStopEvent event = (SimulationStopEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime();
    }

}
