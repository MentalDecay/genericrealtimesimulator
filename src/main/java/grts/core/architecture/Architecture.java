package grts.core.architecture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class Architecture {
    private final Processor[] processors;
    private final CacheHierarchy cacheHierarchy;
    private final MigrationCosts migrationCosts;
    private final Battery battery;
    private final Resources resources;


    public Architecture(HashMap<String, Object> parametersMap) {
        if(parametersMap.get("processors") == null){
            throw new IllegalArgumentException("There is no processor");
        }
        processors = (Processor[]) parametersMap.get("processors");
        cacheHierarchy = (CacheHierarchy) parametersMap.get("cache hierarchy");
        migrationCosts = (MigrationCosts) parametersMap.get("migration costs");
        battery = (Battery) parametersMap.get("battery");
        resources = (Resources) parametersMap.get("resources");
    }

    public Processor[] getProcessors() {
        return processors;
    }

    public Optional<CacheHierarchy> getCacheHierarchy(){
        return Optional.ofNullable(cacheHierarchy);
    }

    public Optional<MigrationCosts> getMigrationCosts(){
        return Optional.ofNullable(migrationCosts);
    }

    public Optional<Battery> getBattery(){
        return Optional.ofNullable(battery);
    }

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
