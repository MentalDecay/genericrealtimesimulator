package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public class StopSimulationEvent extends AbstractEvent implements IEvent {


    public StopSimulationEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

    @Override
    public void doEvent() {
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
}
