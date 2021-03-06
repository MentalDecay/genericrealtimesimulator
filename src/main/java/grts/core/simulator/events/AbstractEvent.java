package grts.core.simulator.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grts.core.simulator.Scheduler;

public abstract class  AbstractEvent implements Event {

    private final Scheduler scheduler;
    private final long time;
    private final int priority;

    /**
     * Creates a new event. The event is linked to a scheduler to handle itself. It also has a time : when the event perform.
     * @param scheduler The scheduler linked to the event to let the event handles itself.
     * @param time The time when the event should perform.
     * @param priority the priority when the event should perform.
     */
    protected AbstractEvent(Scheduler scheduler, long time, int priority) {
        this.scheduler = scheduler;
        this.time = time;
        this.priority = priority;
    }


    /**
     * Comparison between to events. Don't allow to have the same event twice.
     * @param event the event to compare
     * @return a negative integer, zero, or a positive integer as this event is less than, equal to, or greater than the specified event
     */
    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Event event) {
        if(getTime() != event.getTime()){
            long comp = getTime() - event.getTime();
            if(comp < 0){
                return -1;
            }
            else{
                return 1;
            }
        }
        else {
            if (event.getPriority() == getPriority()) {
                if(this.equals(event)){
                    return 0;
                }
                return 1;
            } else {
                return getPriority() - event.getPriority();
            }
        }
    }

    /**
     * Get the scheduler linked to the event. Uses to actualize fields of the scheduler when the event performs.
     * @return The scheduler.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }


    /**
     * Get the time of the event.
     * @return the time of the event.
     */
    @Override
    public long getTime() {
        return time;
    }

    @Override
    public JsonNode toLog() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("event name", getName());
        rootNode.put("time", String.valueOf(getTime()));
        return rootNode;
    }

    /**
     * Get the priority of the event. It's used to sort events. For example, the SimulationStopEvent has the biggest
     * priority because it should perform before the others. Its priority is 1.
     * @return the priority of the event.
     */
    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public abstract String getName();

}