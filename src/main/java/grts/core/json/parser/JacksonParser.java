package grts.core.json.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import grts.core.schedulable.Schedulable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JacksonParser {
    private final TaskParserFactory taskParserFactory = TaskParserFactory.create(taskParserBuilder -> {
        taskParserBuilder.register("PeriodicTaskParser", PeriodicTaskParser::new);
        taskParserBuilder.register("SporadicTaskParser", SporadicTaskParser::new);
    });

    private final HashMap<String, String> taskNameToTaskParserName= new HashMap<>();

    private final JsonNode root;

    /**
     * Creates a new JacksonParser.
     * @param inputStream The inputStream to analyze to parse the json.
     * @throws IOException if there is an error with the file.
     */
    public JacksonParser(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        root = mapper.readValue(inputStream, JsonNode.class);
        taskNameToTaskParserName.put("Periodic task", "PeriodicTaskParser");
        taskNameToTaskParserName.put("Sporadic task", "SporadicTaskParser");
    }

    /**
     * Parse the json from the InputStream.
     * @return A List of tasks.
     */
    public List<Schedulable> parse() {
        LinkedList<Schedulable> tasks = new LinkedList<>();
        JsonNode arrayTasks = root.get("tasks");
        if(arrayTasks == null || !arrayTasks.isArray()){
            System.err.println("Json ill-formed : tasks missing or tasks are not in an array.");
            return tasks;
        }
        for(JsonNode task : arrayTasks){
            if(!task.isObject()){
                System.err.println("Json ill-formed : a task is not a json object");
                return tasks;
            }
            Iterator<Map.Entry<String, JsonNode>> iterator = task.fields();
            if(!iterator.hasNext()){
                System.err.println("Json ill-formed : no field in the task");
                return tasks;
            }
            Map.Entry<String, JsonNode> entryMap = iterator.next();
            System.out.println(entryMap.getKey());
            if(iterator.hasNext()){
                System.err.println("Json ill-formed : multiple fields in the task");
            }
            String taskParserName = taskNameToTaskParserName.get(entryMap.getKey());
            if(taskParserName == null){
                System.err.println("Not implemented task");
            }
            JsonNode taskRoot = task.get(entryMap.getKey());
            TaskParser taskParser = taskParserFactory.create(taskParserName, taskRoot);
            Schedulable taskToAdd = taskParser.newTask();
            tasks.add(taskToAdd);
        }
        return tasks;
    }

}
