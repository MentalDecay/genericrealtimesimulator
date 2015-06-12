package grts.core.processor.policies;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.Processor;

import java.util.*;

public class RestrictedProcessorPolicy implements IProcessorPolicy {

    private class ProcessorAttributes{
        private final LinkedList<Job> activatedJobs = new LinkedList<>();
        private final TreeMap<Long, Double> timeUtilizationMap = new TreeMap<>();
        private double utilization = 0.0;

        private void deleteActivatedJob(Job job){
            activatedJobs.remove(job);
        }

        private double getUtilization() {
            return utilization;
        }

        private void addActivatedJob(Job job){
            activatedJobs.add(job);
        }

        private void putTimeUtilization(long time, double utilization){
            timeUtilizationMap.computeIfPresent(time, (keyLong, valueDouble) -> valueDouble + utilization);
            timeUtilizationMap.computeIfAbsent(time, key -> utilization);
        }

        private void addUtilization(double utilization){
            this.utilization += utilization;
        }

        private void actualizeUtilization(long time){
            while (!timeUtilizationMap.isEmpty() && timeUtilizationMap.firstKey() <= time) {
                utilization -= timeUtilizationMap.pollFirstEntry().getValue();
            }

        }

        public LinkedList<Job> getActivatedJobs() {
            return activatedJobs;
        }
    }


    private final Processor[] processors;
    private final IPriorityPolicy policy;
    private final HashMap<Integer, ProcessorAttributes> processorIdAttributeMap = new HashMap<>();
    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();


    public RestrictedProcessorPolicy(Processor[] processors, IPriorityPolicy policy) {
        this.processors = processors;
        this.policy = policy;
        for(int i = 0; i < processors.length; i++){
            processorIdAttributeMap.put(i, new ProcessorAttributes());
        }
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
        for(Processor processor : processors){
            int processorId = processor.getId();
            List<Job> activatedJobs = processorIdAttributeMap.get(processorId).getActivatedJobs();
            Job jobToExecute = getPriorityPolicy().choseJobToExecute(activatedJobs, time);
            if(jobToExecute == null){
                continue;
            }
            entryList.add(new AbstractMap.SimpleEntry<>(jobToExecute, processorId));
        }
        return entryList;
    }

    @Override
    public void deleteActiveJob(Job job) {
        int processorId = jobProcessorIdMap.get(job);
        jobProcessorIdMap.remove(job);
        processorIdAttributeMap.get(processorId).deleteActivatedJob(job);
    }

    @Override
    public void addActiveJob(Job job) {
        //Actualizes the utilization of processors
        processorIdAttributeMap.forEach((integer, processorAttributes) -> processorAttributes.actualizeUtilization(job.getActivationTime()));

        //Computes the utilization of the job to activate
        double jobUtilization = (double) job.getExecutionTime() / (double) job.getTask().getMinimumInterArrivalTime();
        //Finds the processor where the job should be activated.
//        System.out.println("Utilizations : ");
//        for(Map.Entry<Integer, ProcessorAttributes> entry : processorIdAttributeMap.entrySet()){
//            System.out.println("id : " + entry.getKey());
//            System.out.println("utilization : " + entry.getValue().getUtilization());
//        }
        Optional<Map.Entry<Integer, ProcessorAttributes>> optionalEntry = processorIdAttributeMap.entrySet().stream().
                filter(entry -> entry.getValue().getUtilization() <= 1 - jobUtilization)
                .min(
                        (o1, o2) -> {
                            int cmp = Double.compare(o1.getValue().getUtilization(), o2.getValue().getUtilization());
                            if(cmp == 0){
                                return Integer.compare(processorIdAttributeMap.get(o1.getKey()).getActivatedJobs().size(),
                                        processorIdAttributeMap.get(o2.getKey()).getActivatedJobs().size());
                            }
                            return cmp;
                            //return Double.compare(o1.getValue().getUtilization(), o2.getValue().getUtilization());
                        });
        if(!optionalEntry.isPresent()){
            System.err.println("Can't schedule this job on a processor");
//            TODO throw new Exception and catch it to raise a StopSimulationEvent.
        }
        int processorId = optionalEntry.get().getKey();
        jobProcessorIdMap.put(job, processorId);
        ProcessorAttributes attributes = optionalEntry.get().getValue();
        attributes.addActivatedJob(job);
        attributes.putTimeUtilization(job.getActivationTime() + job.getTask().getMinimumInterArrivalTime(), jobUtilization);
        attributes.addUtilization(jobUtilization);
    }

    @Override
    public void stopJobExecution(Job job, int processorId) {
        Processor processor = processors[processorId];
        processor.stopExecutionJob();
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = processors[processorId];
        processor.executeJob(job);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        Processor processor = processors[processorId];
        return processor.getExecutingJob();
    }


}