package grts.core.json.parser.architecture;

import com.fasterxml.jackson.databind.JsonNode;
import grts.core.architecture.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class ArchitectureParser {
    private final JsonNode root;
    private final JsonNode optionsNode;

    /**
     * Creates a new ArchitectureParse to parse each element of the architecture written in json.
     * @param root The JsonNode root to start the parsing.
     */
    public ArchitectureParser(JsonNode root) {
        if(root == null){
            throw new IllegalArgumentException("Root is null");
        }
        this.root = root.get("architecture");
        if(this.root == null){
            throw new IllegalArgumentException("The json file is ill-formed : there is no architecture");
        }
        optionsNode = this.root.get("options");
    }

    /**
     * Parse the json from the root to get the list of processors.
     * @return An array of processors.
     */
    private Processor[] parseProcessors(){
        JsonNode processorsNode = root.get("processors");
        if(processorsNode == null || !processorsNode.isArray()){
            System.err.println("The json file is ill-formed : there is no array of processors");
            return null;
        }
        int size = processorsNode.size();
        Processor[] processors = new Processor[size];
        for(int i = 0; i < size; i++){
            JsonNode processorNode = processorsNode.get(i);
            if(processorNode == null){
                System.err.println("Error during parsing, there is no more processor");
                return null;
            }
            JsonNode idNode = processorNode.get("id");
            if(idNode == null){
                System.err.println("The json file is ill-formed : there is no id processor");
                return null;
            }
            int id = Integer.parseInt(idNode.textValue());

            JsonNode optionsNode = processorNode.get("options");
            if(optionsNode != null){
                JsonNode speedNode = processorNode.get("speed");
                if(speedNode != null){
//                    TODO get the speed of the processor to create a new processor.
                }
                JsonNode voltageNode = processorNode.get("voltage");
                if(voltageNode != null){
//                    TODO get the voltage of the processor to create a new processor.
                }
            }
            //        TODO change the new to adapt it to the options.
            processors[i] = new Processor(id);
        }
        if(Arrays.stream(processors).map(Processor::getId).distinct().count() != processors.length){
            System.err.println("Two processors have the same id");
        }
        return processors;
    }

    private boolean containsOptions(){
        return optionsNode != null;
    }

    private boolean containsCacheHierarchy(){
        return optionsNode.get("cache hierarchy") != null;
    }

    private CacheHierarchy parseCacheHierarchy(Processor[] processors){
        ArrayList<Cache> caches = new ArrayList<>();
        JsonNode cachesNode = optionsNode.get("cache hierarchy").get("caches");
        if(cachesNode == null){
            System.err.println("The json file is ill-formed : there is no caches in the cache hierarchy");
            return null;
        }
        for(JsonNode cacheNode : cachesNode){
            JsonNode sizeNode = cacheNode.get("size");
            long size = Long.parseLong(sizeNode.textValue());
            JsonNode optionsNode = cacheNode.get("options");
            if(optionsNode == null){
                caches.add(new Cache(size));
                continue;
            }
            JsonNode sharedNode = optionsNode.get("shared");
            if(sharedNode == null){
                System.err.println("The json file is ill-formed : there is no shared options");
                return null;
            }
            JsonNode sharedWithNode = sharedNode.get("shared with");
            if(sharedWithNode == null){
                System.err.println("The json file is ill-formed : there is no shared with");
                return null;
            }
            LinkedList<Integer> sharedWith = new LinkedList<>();
            if(sharedWithNode.isArray()){
                for(JsonNode sharedWithValueNode : sharedWithNode){
                    sharedWith.add(Integer.parseInt(sharedWithValueNode.textValue()));
                }
            }
            else{
                for(Processor processor : processors){
                    sharedWith.add(processor.getId());
                }
            }
            JsonNode optionsPartitioningNode = sharedNode.get("options");
            if(optionsPartitioningNode == null){
                caches.add(new SharedCache(size, sharedWith));
                continue;
            }
            JsonNode partitioningNode = optionsPartitioningNode.get("partitioning");
            if(partitioningNode == null){
                System.err.println("Json file is ill-formed : partitioning missing");
                return null;
            }
            JsonNode numberPartitionsNode = partitioningNode.get("number of partitions");
            if(numberPartitionsNode == null){
                System.err.println("Json file is ill-formed : number of partitions missing");
                return null;
            }
            int numberPartitions = Integer.parseInt(numberPartitionsNode.textValue());
            JsonNode optionsAssignationNode = partitioningNode.get("options");
            if(optionsAssignationNode == null){
                caches.add(new PartitionedSharedCache(size, sharedWith, numberPartitions));
                continue;
            }
            JsonNode assignationsNode = optionsAssignationNode.get("assignations of partitions");
            if(assignationsNode == null){
                System.err.println("Json file is ill-formed : assignations of partitions is missing");
                return null;
            }
            if(!assignationsNode.isArray() || assignationsNode.size() != numberPartitions){
                System.err.println("Json file is ill-formed : assignations is not an array or there is no as many assignations as partitions");
                return null;
            }
            ArrayList<LinkedList<Integer>> assignations = new ArrayList<>();
            for(JsonNode assignation : assignationsNode){
                LinkedList<Integer> assignationList = new LinkedList<>();
                assignation.forEach(jsonNode -> assignationList.add(Integer.parseInt(jsonNode.textValue())));
                assignations.add(assignationList);
            }
            caches.add(new PartitionedSharedCache(size, sharedWith, numberPartitions, assignations));
        }
        return new CacheHierarchy(caches);
    }

    private boolean containsMigrationCosts(){
        return optionsNode.get("migration cost") != null;
    }

    private MigrationCosts parseMigrationCosts(int nbProcessor){
        MigrationCosts migrationCosts = new MigrationCosts();

        JsonNode migrationCostsNode = optionsNode.get("migration cost");
        if(!migrationCostsNode.isArray()){
            System.err.println("The json file is ill-formed : the migration costs is not an array");
            return null;
        }
        for(JsonNode jsonNode : migrationCostsNode){
            JsonNode fromNode = jsonNode.get("from");
            JsonNode toNode = jsonNode.get("to");
            JsonNode costNode = jsonNode.get("cost");
            if(fromNode == null || toNode == null || costNode == null){
                System.err.println("The json file is ill-formed : migrations costs without from / to / cost");
                return null;
            }
            int from = Integer.parseInt(fromNode.textValue());
            int to = Integer.parseInt(toNode.textValue());
            long cost = Long.parseLong(costNode.textValue());
            migrationCosts.addMigrationCost(from, to, cost);
        }
        if(migrationCosts.nbMigrationCosts() != nbProcessor * nbProcessor){
            System.err.println("The json file is ill-formed : there are not (number of processors)^2 migrations costs");
            return null;
        }
        return migrationCosts;
    }

    private boolean containsBattery(){
        return optionsNode.get("battery") != null;
    }

    private Battery parseBattery(){
        JsonNode batteryNode = optionsNode.get("battery");
        JsonNode capacityNode = batteryNode.get("capacity");
        JsonNode reloadNode = batteryNode.get("reloading");
        JsonNode dischargeNode = batteryNode.get("discharging");
        if(capacityNode == null || reloadNode == null || dischargeNode == null){
            System.err.println("The json file is ill-formed : battery without capacity / reloading / discharging");
            return null;
        }
        long capacity = Long.parseLong(capacityNode.textValue());
        String reload = reloadNode.textValue();
        String discharge = dischargeNode.textValue();
        return new Battery(capacity, reload, discharge);
    }

    private boolean containsResources(){
        return optionsNode.get("resources") != null;
    }

    private Resources parseResources(){
        JsonNode resourcesNode = optionsNode.get("resources");
        if(!resourcesNode.isArray()){
            System.err.println("The json file is ill-formed : resources is not an array");
            return null;
        }
        Resources resources = new Resources();
        for(JsonNode resourceNode : resourcesNode){
            JsonNode typeNode = resourceNode.get("type");
            JsonNode idNode = resourceNode.get("id");
            if(typeNode == null || idNode == null){
                System.err.println("The json file is ill-formed : resources without type / id");
                return null;
            }
            String type = typeNode.textValue();
            String id = idNode.textValue();
            resources.addResource(type, id);
        }
        return resources;
    }

    /**
     * Parse the architecture from the JsonNode given as root.
     * @return A new Architecture created from the json.
     */
    public Architecture parse(){
        Processor[] processors = parseProcessors();
        if(processors == null){
            System.err.println("There is no processor");
            return null;
        }
        HashMap<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("processors", processors);
        if(containsOptions()) {
            if (containsCacheHierarchy()) {
                CacheHierarchy cacheHierarchy = parseCacheHierarchy(processors);
                parametersMap.put("cache hierarchy", cacheHierarchy);
            }
            if (containsMigrationCosts()){
                MigrationCosts migrationCosts = parseMigrationCosts(processors.length);
                parametersMap.put("migration costs", migrationCosts);
            }
            if (containsBattery()){
                Battery battery = parseBattery();
                parametersMap.put("battery", battery);
            }
            if (containsResources()){
                Resources resources = parseResources();
                parametersMap.put("resources", resources);
            }
        }
        return new Architecture(parametersMap);
    }
}
