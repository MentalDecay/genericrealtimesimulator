package grts.core.processor.policies;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.Processor;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MonoProcessor implements IProcessorPolicy {

    private final Processor processor;
    private final IPriorityPolicy policy;
    private final List<Job> activatedJobs =  new LinkedList<>();

    public MonoProcessor(IPriorityPolicy policy) {
        this.processor = new Processor(0);
        this.policy = policy;
    }


    @Override
    public Processor[] getProcessors() {
        Processor[] p = new Processor[1];
        p[0] = processor;
        return p;
    }

    @Override
    public IPriorityPolicy getPriorityPolicy() {
        return policy;
    }

    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        Job jobToExecute = policy.choseJobToExecute(activatedJobs, time);
        if(jobToExecute == null){
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
        processor.stopExecutionJob();
    }

    @Override
    public void executeJob(Job job, int processorId) {
        processor.executeJob(job);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        return processor.getExecutingJob();
    }

}
