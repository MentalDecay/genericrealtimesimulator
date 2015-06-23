package grts.core.processor.policies;

import grts.core.architecture.Architecture;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.architecture.Processor;

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


    private final Architecture architecture;
    private final IPriorityPolicy policy;
    private final HashMap<Integer, ProcessorAttributes> processorIdAttributeMap = new HashMap<>();
    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();


    /**
     * Creates a new restricted processor policy. According to this policy, each processor has a private activated jobs list. The purpose of this policy is to chose
     * where to add the new activated job. The job is added to the processor with the lowest utilization.
     * @param architecture The architecture used for the simulation.
     * @param policy The priority policy associated to the processor policy.
     */
    public RestrictedProcessorPolicy(Architecture architecture, IPriorityPolicy policy) {
        this.architecture = architecture;
        this.policy = policy;
        for(int i = 0; i < architecture.getProcessors().length; i++){
            processorIdAttributeMap.put(i, new ProcessorAttributes());
        }
    }


    @Override
    public Processor[] getProcessors() {
        return architecture.getProcessors();
    }

    @Override
    public IPriorityPolicy getPriorityPolicy() {
        return policy;
    }

    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        List<AbstractMap.SimpleEntry<Job, Integer>> entryList = new LinkedList<>();
        for(Processor processor : architecture.getProcessors()){
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
                        });
        if(!optionalEntry.isPresent()){
            System.err.println("Can't schedule this job on a processor");
//            TODO throw new Exception and catch it to raise a SimulationStopEvent.
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
        Processor processor = architecture.getProcessors()[processorId];
        processor.stopExecutionJob();
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.executeJob(job);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        return processor.getExecutingJob();
    }


}