package grts.core.architecture;

public class Battery {
    private final long capacity;
    private final String reload;
    private final String discharge;

    /**
     * Creates a new Battery.
     * @param capacity The capacity of the battery.
     * @param reload The reload function of the battery.
     * @param discharge The discharge function of the battery.
     */
    public Battery(long capacity, String reload, String discharge) {
        this.capacity = capacity;
        this.reload = reload;
        this.discharge = discharge;
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
    public String getDischarge() {
        return discharge;
    }

    /**
     * Get the name of the reloading function.
     * @return The name of the reloading function.
     */
    public String getReload() {
        return reload;
    }

    @Override
    public String toString() {
        return "Battery : \n\tCapacity : " + capacity +
                "\n\tReload : " + reload +
                "\n\tDischarge : " + discharge;
    }
}
