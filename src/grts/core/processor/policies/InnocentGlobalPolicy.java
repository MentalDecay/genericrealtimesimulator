package grts.core.processor.policies;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.Processor;
import sun.awt.image.ImageWatched;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InnocentGlobalPolicy implements IProcessorPolicy {

    private final Processor[] processors;
    private final IPriorityPolicy policy;
    private final List<Job> activatedJobs =  new LinkedList<>();
    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();

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

//        for (Processor ignored : processors) {
//            if (copyActivatedJobs.size() == 0) {
//                continue;
//            }
//            Job job = copyActivatedJobs.remove(0);
//
//            selectedJobs.add(job);
//        }
//
//        System.out.println("selected jobs : " + selectedJobs);
//        for(Processor processor : processors){
//            if(selectedJobs.contains(processor.getExecutingJob())){
//                continue;
//            }
//            if(selectedJobs.size() == 0){
//                break;
//            }
//            entryList.add(new AbstractMap.SimpleEntry<>(selectedJobs.remove(0), processor.getId()));
//        }
//        if(selectedJobs.size() != 0){
//            throw new IllegalStateException("Selected jobs remain");
//        }
        int cmpt = 0;
        int max = processors.length;
        List<Integer> alreadyExecutingId = new LinkedList<>();
        System.out.println("activated jobs : " + copyActivatedJobs);
        while(cmpt < max){
            if(copyActivatedJobs.size() == 0){
                break;
            }
            Job job = getPriorityPolicy().choseJobToExecute(copyActivatedJobs, time);
            System.out.println("most priority job : job : " + job);
            copyActivatedJobs.remove(job);
            if(jobProcessorIdMap.containsKey(job)){
                alreadyExecutingId.add(jobProcessorIdMap.get(job));
            }
            else{
                selectedJobs.add(job);
            }
            cmpt++;
        }
        System.out.println("executing jobs : " + jobProcessorIdMap);
        System.out.println("selected jobs : " + selectedJobs);
        for(Processor processor : processors){
            if(selectedJobs.size() == 0){
                System.out.println("shit");
                break;
            }
            if(alreadyExecutingId.contains(processor.getId())){
                System.out.println("fuck");
                continue;
            }
            System.out.println("what");
            entryList.add(new AbstractMap.SimpleEntry<>(selectedJobs.remove(0), processor.getId()));
        }
        if(selectedJobs.size() != 0){
            throw new IllegalStateException("Selected jobs remain");
        }
        System.out.println("entry list : " + entryList);
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
        System.out.println("removing");
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = processors[processorId];
        processor.executeJob(job);
        jobProcessorIdMap.put(job, processorId);
        System.out.println("adding");
    }

    @Override
    public Job getExecutingJob(int processorId) {
        Processor processor = processors[processorId];
        return processor.getExecutingJob();
    }

}
