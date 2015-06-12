package grts.core.taskset;

import grts.core.json.parser.JacksonParser;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.SporadicTask;
import grts.core.schedulable.TaskFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface TaskSetFactory {

    static ITaskSet createTaskSetFromFile(String path) throws IOException {
        TaskFactory factory = TaskFactory.create(builder -> {
            builder.register("Periodic Task", PeriodicTask::new);
            builder.register("Sporadic Task", SporadicTask::new);
        });
        InputStream inputStream = Files.newInputStream(Paths.get(path));
        JacksonParser jacksonParser = new JacksonParser(inputStream);
        inputStream.close();
        return new TaskSet(jacksonParser.parse());
    }
}
