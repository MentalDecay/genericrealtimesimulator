package grts.core.architecture;

public class Battery {
    private final long capacity;
    private final long reload;
    private final long discharge;
    private long status;

    /**
     * Creates a new Battery.
     * @param capacity The capacity of the battery.
     * @param reload The reload function of the battery.
     * @param discharge The discharge function of the battery.
     * @param initialStatus The initial status of the battery.
     */
    public Battery(long capacity, long reload, long discharge, long initialStatus) {
        this.capacity = capacity;
        this.reload = reload;
        this.discharge = discharge;
        this.status = initialStatus;
    }

    /**
     * Get the capacity of the battery.
     * @return The capacity of the battery.
     */
    public long getCapacity() {
        return capacity;
    }

    /**
     * Get the name of the discharging function.
     * @return The name of the discharging function.
     */
    public long getDischarge() {
        return discharge;
    }

    /**
     * Get the name of the reloading function.
     * @return The name of the reloading function.
     */
    public long getReload() {
        return reload;
    }

    /**
     * Get the status of the battery.
     * @return The status of the battery.
     */
    public long getStatus() {
        return status;
    }

    /**
     * Reloads the battery.
     */
    public void reload() {
        status = (status + reload > capacity) ? capacity : status + reload;
    }

    /**
     * Discharges the battery.
     * @param discharge The discharge to apply.
     */
    public void discharge(long discharge) {
        if (status < discharge) {
            throw new IllegalArgumentException("not enough battery");
        } else {
            status -= discharge;
        }
    }

    @Override
    public String toString() {
        return "Battery : \n\tCapacity : " + capacity +
                "\n\tReload : " + reload +
                "\n\tDischarge : " + discharge;
    }
}
