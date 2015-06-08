package grts.core.simulator.events;

public interface IEvent {
    /**
     * Get the time of the event.
     * @return the time of the event
     */
    long getTime();

    /**
     * Performs the event.
     */
    void doEvent();
}
