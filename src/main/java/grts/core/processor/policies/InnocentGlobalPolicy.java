package grts.core.processor.policies;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.architecture.Processor;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InnocentGlobalPolicy implements IProcessorPolicy {

    private final Processor[] processors;
    private final IPriorityPolicy policy;
    private final List<Job> activatedJobs =  new LinkedList<>();
    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();

    /**
     * Creates a new Innocent Global Policy. This policy chose the first idle processor if there is one or the first processor with a task which shouldn't
     * be executing according to the priority policy associated with this processor policy.
     * @param processors An array of processors.
     * @param policy The priority policy associated with the processor policy.
     */
    public InnocentGlobalPolicy(Processor[] processors, IPriorityPolicy policy) {
        this.processors = processors;
        this.policy = policy;
    }


    @Override
    public Processor[] getProcessors() {
        return processors;
    }

    @Override
    public IPriorityPolicy getPriorityPolicy() {
        return policy;
    }

    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        List<AbstractMap.SimpleEntry<Job, Integer>> entryList = new LinkedList<>();
        List<Job> copyActivatedJobs = new LinkedList<>(activatedJobs);
        List<Job> selectedJobs = new LinkedList<>();

        int cmpt = 0;
        int max = processors.length;
        List<Integer> alreadyExecutingId = new LinkedList<>();
        while(cmpt < max){
            if(copyActivatedJobs.size() == 0){
                break;
            }
            Job job = getPriorityPolicy().choseJobToExecute(copyActivatedJobs, time);
            copyActivatedJobs.remove(job);
            if(jobProcessorIdMap.containsKey(job)){
                alreadyExecutingId.add(jobProcessorIdMap.get(job));
            }
            else{
                selectedJobs.add(job);
            }
            cmpt++;
        }
        for(Processor processor : processors){
            if(selectedJobs.size() == 0){
                break;
            }
            if(alreadyExecutingId.contains(processor.getId())){
                continue;
            }
            entryList.add(new AbstractMap.SimpleEntry<>(selectedJobs.remove(0), processor.getId()));
        }
        if(selectedJobs.size() != 0){
            throw new IllegalStateException("Selected jobs remain");
        }
        return entryList;
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
        Processor processor = processors[processorId];
        processor.stopExecutionJob();
        jobProcessorIdMap.remove(job, processorId);
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = processors[processorId];
        processor.executeJob(job);
        jobProcessorIdMap.put(job, processorId);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        Processor processor = processors[processorId];
        return processor.getExecutingJob();
    }

}
