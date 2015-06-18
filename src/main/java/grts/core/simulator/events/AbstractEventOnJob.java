package grts.core.simulator.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public abstract class AbstractEventOnJob  extends AbstractEvent implements Event {

    private final Job job;
    private final int processorId;


    /**
     * Creates a new event linked to a job.
     * @param scheduler the scheduler linked to the event.
     * @param time the time of the event.
     * @param job the job of the event.
     */
    protected AbstractEventOnJob(Scheduler scheduler, long time, Job job) {
        super(scheduler, time);
        this.job = job;
        this.processorId = -1;
    }

    protected AbstractEventOnJob(Scheduler scheduler, long time, Job job, int processorId) {
        super(scheduler, time);
        this.job = job;
        this.processorId = processorId;
    }


    /**
     * Get the job of the event.
     * @return the job of the event.
     */
    public Job getJob() {
        return job;
    }

    /**
     * Get the id of the processor where the job is scheduled.
     * @return the id of the processor where the job is scheduled.
     */
    public int getProcessorId() {
        return processorId;
    }

    @Override
    public abstract String getName();

    @Override
    public JsonNode toLog() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("event name", getName());
        rootNode.put("time", String.valueOf(getTime()));
        ObjectNode optionsNode = mapper.createObjectNode();
        ObjectNode jobNode = mapper.createObjectNode();
        jobNode.put("id", String.valueOf(job.getJobId()));
        jobNode.put("task", job.getTask().getName());
        optionsNode.set("job", jobNode);
        if(processorId == -1){
            optionsNode.put("processor", "no value attributed yet");
        }
        else {
            optionsNode.put("processor", String.valueOf(processorId));
        }
        rootNode.set("options", optionsNode);
        return rootNode;
    }
}
