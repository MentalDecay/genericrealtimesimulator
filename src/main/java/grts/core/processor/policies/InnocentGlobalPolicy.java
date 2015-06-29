package grts.core.processor.policies;

import grts.core.architecture.Architecture;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.architecture.Processor;
import grts.core.taskset.TaskSet;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InnocentGlobalPolicy implements IProcessorPolicy {

    private final Architecture architecture;
    private final IPriorityPolicy policy;
    private final List<Job> activatedJobs =  new LinkedList<>();
    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();

    /**
     * Creates a new Innocent Global Policy. This policy chose the first idle processor if there is one or the first processor with a task which shouldn't
     * be executing according to the priority policy associated with this processor policy.
     * @param architecture The architecture used for the simulation.
     * @param policy The priority policy associated with the processor policy.
     */
    public InnocentGlobalPolicy(Architecture architecture, IPriorityPolicy policy) {
        this.architecture = architecture;
        this.policy = policy;
    }


    @Override
    public Processor[] getProcessors() {
        return architecture.getProcessors();
    }


    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        List<AbstractMap.SimpleEntry<Job, Integer>> entryList = new LinkedList<>();
        List<Job> copyActivatedJobs = new LinkedList<>(activatedJobs);
        List<Job> selectedJobs = new LinkedList<>();

        int cmpt = 0;
        int max = architecture.getProcessors().length;
        List<Integer> alreadyExecutingId = new LinkedList<>();
        while(cmpt < max){
            if(copyActivatedJobs.size() == 0){
                break;
            }
            Job job = policy.choseJobToExecute(copyActivatedJobs, time);
            copyActivatedJobs.remove(job);
            if(jobProcessorIdMap.containsKey(job)){
                alreadyExecutingId.add(jobProcessorIdMap.get(job));
            }
            else{
                selectedJobs.add(job);
            }
            cmpt++;
        }
        for(Processor processor : architecture.getProcessors()){
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
        Processor processor = architecture.getProcessors()[processorId];
        processor.stopExecutionJob();
        jobProcessorIdMap.remove(job, processorId);
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.executeJob(job);
        jobProcessorIdMap.put(job, processorId);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        return processor.getExecutingJob();
    }

    @Override
    public TaskSet initTaskSet(TaskSet taskSet) {
        return taskSet;
    }

}
