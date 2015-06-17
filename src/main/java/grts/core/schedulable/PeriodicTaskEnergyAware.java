package grts.core.schedulable;

import java.util.Map;

public class PeriodicTaskEnergyAware extends PeriodicTask {
    private final long wcec;

    /**
     * Creates a new PeriodicTask which is aware of the energy element. The only energy element is the Worst Case Execution Consumption yet.
     * @param map The map of String and Object which contains the minimumInterArrivalTime (long), the wcet (long), the deadline (long), the offset (long),
     * the name (String) and the wcec (long).
     */
    public PeriodicTaskEnergyAware(Map<String, Object> map) {
        super(map);
        if(map.get("wcec") == null){
            throw new IllegalArgumentException("Wcec missing");
        }
        wcec = (long) map.get("wcec");
    }

    /**
     * Creates a new PeriodicTask which is aware of the energy element.
     * @param period The period of the task.
     * @param wcet The Worst Case Execution Time of the task.
     * @param deadline The deadline of the task.
     * @param offset The offset of the task.
     * @param name The name of the task.
     * @param wcec The Worst Case Energy Consumption of the task.
     */
    public PeriodicTaskEnergyAware(long period, long wcet, long deadline, long offset, String name, long wcec) {
        super(period, wcet, deadline, offset, name);
        this.wcec = wcec;
    }

    /**
     * Get the Worst Case Execution Consumption
     * @return the wcec of the task.
     */
    public long getWcec() {
        return wcec;
    }
}
