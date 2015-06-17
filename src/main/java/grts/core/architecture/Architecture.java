package grts.core.architecture;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class Architecture {
    private final Processor[] processors;
    private final CacheHierarchy cacheHierarchy;
    private final MigrationCosts migrationCosts;
    private final Battery battery;
    private final Resources resources;


    /**
     * Creates a new Architecture. Only one field is required in the map : the Processor array. The others are optionals.
     * Optional fields :
     * "cache hierarchy" with a CacheHierarchy as value.
     * "migration costs" with a MigrationCosts as value.
     * "battery" with a Battery as value.
     * "resources" with a Resources as value.
     * @param parametersMap A map of String, Object which contains the parameters given by name. The field "processor" with as value an array of Processors is required.
     */
    public Architecture(Map<String, Object> parametersMap) {
        if(parametersMap.get("processors") == null){
            throw new IllegalArgumentException("There is no processor");
        }
        processors = (Processor[]) parametersMap.get("processors");
        cacheHierarchy = (CacheHierarchy) parametersMap.get("cache hierarchy");
        migrationCosts = (MigrationCosts) parametersMap.get("migration costs");
        battery = (Battery) parametersMap.get("battery");
        resources = (Resources) parametersMap.get("resources");
    }

    /**
     * Get the array of processors.
     * @return The array of processors.
     */
    public Processor[] getProcessors() {
        return processors;
    }

    /**
     * Get the cache hierarchy.
     * @return An Optional CacheHierarchy (this field is not required).
     */
    public Optional<CacheHierarchy> getCacheHierarchy(){
        return Optional.ofNullable(cacheHierarchy);
    }

    /**
     * Get the migration costs.
     * @return An Optional MigrationCosts (this field is not required).
     */
    public Optional<MigrationCosts> getMigrationCosts(){
        return Optional.ofNullable(migrationCosts);
    }

    /**
     * Get the battery.
     * @return An Optional Battery (this field is not required).
     */
    public Optional<Battery> getBattery(){
        return Optional.ofNullable(battery);
    }

    /**
     * Get the resources
     * @return An Optional Resources (this field is not required).
     */
    public Optional<Resources> getResources(){
        return Optional.ofNullable(resources);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Architecture :\n");
        Arrays.stream(processors).forEach(processor -> stringBuilder.append("\t").append(processor).append("\n"));
        if(cacheHierarchy != null)
            stringBuilder.append(cacheHierarchy).append("\n");
        if(migrationCosts != null)
            stringBuilder.append(migrationCosts).append("\n");
        if(battery != null){
            stringBuilder.append(battery).append("\n");
        }
        if(resources != null){
            stringBuilder.append(resources).append("\n");
        }
        return stringBuilder.toString();
    }
}
