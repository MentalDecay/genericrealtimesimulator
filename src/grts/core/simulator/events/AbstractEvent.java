package grts.core.simulator.events;

import grts.core.simulator.Scheduler;

public abstract class  AbstractEvent implements Comparable<AbstractEvent>, IEvent {

    private final Scheduler scheduler;
    private final long time;

    /**
     * Creates a new event. The event is linked to a scheduler to handle itself. It also has a time : when the event perform.
     * @param scheduler The scheduler linked to the event to let the event handles itself.
     * @param time The time when the event should perform.
     */
    protected AbstractEvent(Scheduler scheduler, long time) {
        this.scheduler = scheduler;
        this.time = time;
    }


    /**
     * Comparison between to events. Don't allow to have the same event twice.
     * @param o the event to compare.
     * @return a negative integer, zero, or a positive integer as this event is less than, equal to, or greater than the specified event.
     */
    @Override
    public int compareTo(AbstractEvent o) {
        if(getTime() != o.getTime()){
            //return (int) (getTime() - o.getTime());
            long comp = getTime() - o.getTime();
            if(comp < 0){
                return -1;
            }
            else{
                return 1;
            }
        }
        else {
            if (o.getPriority() == getPriority()) {
                if(this.equals(o)){
                    return 0;
                }
                return 1;
            } else {
                return getPriority() - o.getPriority();
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

    /**
     * Get the priority of the event. It's used to sort events. For example, the StopSimulationEvent has the biggest priority because it should perform before the others.
     * Its priority is 1.
     * @return
     */
    protected abstract int getPriority();

}
