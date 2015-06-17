package grts.core.json.parser.task;

import com.fasterxml.jackson.databind.JsonNode;
import grts.core.schedulable.Schedulable;

import java.util.HashMap;

public class PeriodicTaskParser extends AbstractTaskParser implements TaskParser{
    private final JsonNode root;

    /**
     * Creates a new PeriodicTaskParse to parse a periodic task.
     * @param root The JsonNode where the task begins.
     */
    public PeriodicTaskParser(JsonNode root) {
        this.root = root;
    }

    @Override
    public Schedulable newTask() {
        HashMap<String, Object> parametersMap = new HashMap<>();
        JsonNode wcetNode = root.get("wcet");
        if(wcetNode == null){
            System.err.println("Json is ill-formed : no wcet for a periodic task");
            return null;
        }
        long wcet = Long.parseLong(wcetNode.textValue());

        JsonNode periodNode = root.get("period");
        if(periodNode == null){
            System.err.println("Json is ill-formed : no period for a periodic task");
            return null;
        }
        long minimumInterArrivalTime = Long.parseLong(periodNode.textValue());

        JsonNode nameNode = root.get("name");
        if(nameNode == null){
            System.err.println("Json is ill-formed : no name for a periodic task");
            return null;
        }
        String name = nameNode.textValue();

        parametersMap.put("wcet", wcet);
        parametersMap.put("minimumInterArrivalTime",  minimumInterArrivalTime);
        parametersMap.put("name", name);
        JsonNode optionsNode = root.get("options");

        long offset = 0;
        long deadline = minimumInterArrivalTime;

        if(optionsNode != null){
            JsonNode offsetNode = optionsNode.get("offset");
            if(offsetNode != null){
                offset = Long.parseLong(offsetNode.textValue());
            }
            parametersMap.put("offset", offset);
            JsonNode deadlineNode = optionsNode.get("deadline");
            if(deadlineNode != null){
                deadline = Long.parseLong(deadlineNode.textValue());
            }
            parametersMap.put("deadline", deadline);
            JsonNode energyNode = optionsNode.get("energy");
            if(energyNode != null){
                JsonNode wcecNode = energyNode.get("wcec");
                long wcec = Long.parseLong(wcecNode.textValue());
                System.out.println("wcec : " + wcec);
                parametersMap.put("wcec", wcec);
                return factory.create("PeriodicTaskEnergyAware", parametersMap);
            }
            JsonNode memoryNode = optionsNode.get("memory");
            if(memoryNode != null) {
                long memory = Long.parseLong(memoryNode.textValue());
                parametersMap.put("memory", memory);
                System.out.println("memory : " + memory);
                return factory.create("PeriodicTaskMemoryAware", parametersMap);
            }
            JsonNode sharedMemoryNode = optionsNode.get("shared memory");
            if(sharedMemoryNode != null){
                System.out.println("Shared memory element found but not implemented yet");
                processSharedMemoryNode(sharedMemoryNode, parametersMap);
            }
        }
        parametersMap.put("offset", offset);
        parametersMap.put("deadline", deadline);
        System.out.println("ParametersMap : " + parametersMap);
        return factory.create("PeriodicTask", parametersMap);
    }

    private void processSharedMemoryNode(JsonNode sharedMemoryNode, HashMap<String, Object> parametersMap){
        for(JsonNode sharedMemoryObject : sharedMemoryNode){
            JsonNode fromNode = sharedMemoryObject.get("from");
            JsonNode toNode = sharedMemoryObject.get("to");
            JsonNode resourceNode = sharedMemoryObject.get("resource");
            if(fromNode == null || toNode == null || resourceNode == null){
                System.err.println("Json ill-formed : shared resource without from / to / resource");
                return;
            }
            System.out.println("from : " + fromNode.textValue());
            System.out.println("to : " + toNode.textValue());
            System.out.println("resource id : " + resourceNode.textValue());
            //TODO memory node
        }

    }
}
