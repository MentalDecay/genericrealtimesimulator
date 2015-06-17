package grts.core.json.parser.task;

import com.fasterxml.jackson.databind.JsonNode;
import grts.core.schedulable.Schedulable;

import java.util.HashMap;

public class SporadicTaskParser extends AbstractTaskParser implements TaskParser {
    private final JsonNode root;

    /**
     * Creates a new SporadicTaskParse to parse a sporadic task.
     * @param root The JsonNode where the task begins.
     */
    public SporadicTaskParser(JsonNode root) {
        this.root = root;
    }

    @Override
    public Schedulable newTask() {
        HashMap<String, Object> parametersMap = new HashMap<>();
        JsonNode wcetNode = root.get("wcet");
        if(wcetNode == null){
            System.err.println("Json is ill-formed : no wcet for a sporadic task");
            return null;
        }
        long wcet = Long.parseLong(wcetNode.textValue());

        JsonNode minimumInterArrivalNode = root.get("minimum inter arrival time");
        if(minimumInterArrivalNode == null){
            System.err.println("Json is ill-formed : no minimum inter arrival time for a sporadic task");
            return null;
        }
        long minimumInterArrivalTime = Long.parseLong(minimumInterArrivalNode.textValue());

        JsonNode nameNode = root.get("name");
        if(nameNode == null){
            System.err.println("Json is ill-formed : no name for a sporadic task");
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
            JsonNode deadlineNode = optionsNode.get("deadline");
            if(deadlineNode != null){
                deadline = Long.parseLong(deadlineNode.textValue());
            }
            JsonNode energyNode = optionsNode.get("energy");
            if(energyNode != null){
                System.out.println("Energy element found but not implemented yet");
//                TODO energy node
            }
            JsonNode memoryNode = optionsNode.get("memory");
            if(memoryNode != null){
                System.out.println("Memory element found but not implemented yet");
                long memory = Long.parseLong(memoryNode.textValue());
                System.out.println("memory : " + memory);
//                TODO memory node
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
        return factory.create("SporadicTask", parametersMap);
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
