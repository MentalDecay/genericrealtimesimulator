package grts.core.simulator.events;

public interface IEvent {
    long getTime();

    void doEvent();
}
