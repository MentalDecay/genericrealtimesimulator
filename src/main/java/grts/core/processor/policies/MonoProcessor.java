package grts.core.processor.policies;

import grts.core.architecture.Architecture;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.architecture.Processor;
import grts.core.taskset.TaskSet;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MonoProcessor implements IProcessorPolicy {

    private final Architecture architecture;
    private final IPriorityPolicy policy;
    private final List<Job> activatedJobs = new LinkedList<>();

    /**
     * Creates a new mono processor policy.
     *
     * @param policy The priority policy associated with the mono processor policy.
     */
    public MonoProcessor(Architecture architecture, IPriorityPolicy policy) {
        this.architecture = architecture;
        if (architecture.getProcessors().length != 1) {
            throw new IllegalArgumentException("The architecture contains more than 1 processor");
        }
//        this.processor = new Processor(0);
        this.policy = policy;
    }


    @Override
    public Processor[] getProcessors() {
        return architecture.getProcessors();
    }


    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        Job jobToExecute = policy.choseJobToExecute(activatedJobs, time);
        if (jobToExecute == null) {
            return Collections.emptyList();
        }
        List<AbstractMap.SimpleEntry<Job, Integer>> list = new LinkedList<>();
        list.add(new AbstractMap.SimpleEntry<>(jobToExecute, 0));
        return list;
    }

    @Override
    public void deleteActiveJob(Job job) {
        activatedJobs.remove(job);
    }

    @Override
    public void addActiveJob(Job job) {
        activatedJobs.add(job);
    }

    @Override
    public void stopJobExecution(Job job, int processorId) {
        architecture.getProcessors()[0].stopExecutionJob();
    }

    @Override
    public void executeJob(Job job, int processorId) {
        architecture.getProcessors()[0].executeJob(job);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        return architecture.getProcessors()[0].getExecutingJob();
    }

    @Override
    public TaskSet initTaskSet(TaskSet taskSet) {
        return taskSet;
    }

}
