package grts.core.schedulable;

import java.util.Map;

public class PeriodicTaskEnergyAware extends PeriodicTask {
    private final long wcec;

    public PeriodicTaskEnergyAware(Map<String, Object> map) {
        super(map);
        if(map.get("wcec") == null){
            throw new IllegalArgumentException("Wcec missing");
        }
        wcec = (long) map.get("wcec");
    }

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
