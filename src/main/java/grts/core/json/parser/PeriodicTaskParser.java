package grts.core.json.parser;

import com.fasterxml.jackson.databind.JsonNode;
import grts.core.schedulable.Schedulable;

import java.util.HashMap;

public class PeriodicTaskParser extends AbstractTaskParser implements TaskParser{
    private final JsonNode root;

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
            JsonNode deadlineNode = optionsNode.get("deadline");
            if(deadlineNode != null){
                deadline = Long.parseLong(deadlineNode.textValue());
            }
        }
        parametersMap.put("offset", offset);
        parametersMap.put("deadline", deadline);
        System.out.println("ParametersMap : " + parametersMap);
        return factory.create("PeriodicTask", parametersMap);
    }
}
