package grts.core.json.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import grts.core.architecture.Architecture;
import grts.core.json.parser.architecture.ArchitectureParser;
import grts.core.json.parser.task.PeriodicTaskParser;
import grts.core.json.parser.task.SporadicTaskParser;
import grts.core.json.parser.task.TaskParser;
import grts.core.json.parser.task.TaskParserFactory;
import grts.core.schedulable.Schedulable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SimulatorJacksonParser {

    private final TaskParserFactory taskParserFactory = TaskParserFactory.create(taskParserBuilder -> {
        taskParserBuilder.register("PeriodicTaskParser", PeriodicTaskParser::new);
        taskParserBuilder.register("SporadicTaskParser", SporadicTaskParser::new);
    });

    private final HashMap<String, String> taskNameToTaskParserName= new HashMap<>();
    private final ArchitectureParser architectureParser;

    private final JsonNode rootTaskParser;

    /**
     * Creates a new SimulatorJacksonParser.
     * @param inputStreamTaskSet The inputStream to analyze to parse the tasks set from the json.
     * @param inputStreamArchitecture The inputStream to analyze to parse the architecture from the json.
     * @throws IOException if there is an error with the file.
     */
    public SimulatorJacksonParser(InputStream inputStreamTaskSet, InputStream inputStreamArchitecture) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        rootTaskParser = mapper.readValue(inputStreamTaskSet, JsonNode.class);

        taskNameToTaskParserName.put("Periodic task", "PeriodicTaskParser");
        taskNameToTaskParserName.put("Sporadic task", "SporadicTaskParser");

        ObjectMapper mapperArchitecture = new ObjectMapper();
        architectureParser = new ArchitectureParser(mapperArchitecture.readValue(inputStreamArchitecture, JsonNode.class));
        inputStreamArchitecture.close();
        inputStreamTaskSet.close();
    }

    /**
     * Parse the json from the inputStreamTaskSet to get the list of tasks.
     * @return A List of tasks.
     */
    public List<Schedulable> parseTasks() {
        LinkedList<Schedulable> tasks = new LinkedList<>();
        JsonNode arrayTasks = rootTaskParser.get("tasks");
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

    /**
     * Creates the architecture from the json file.
     * @return A new architecture created from the json file.
     */
    public Architecture parseArchitecture(){
        return architectureParser.parse();
    }
}
