package grts.core.processor.policies;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.Processor;

import java.util.AbstractMap;
import java.util.List;

public interface IProcessorPolicy {

    /**
     * Get the array of processors.
     * @return the array of processors.
     */
    Processor[] getProcessors();

    /**
     * Get the priority policy of the Processor Policy.
     * @return the priority policy.
     */
    IPriorityPolicy getPriorityPolicy();

    /**
     * Select the jobs to execute and the processors to execute them according to the priority policy and the processor policy.
     * @param time the time when the selection has to be made.
     * @return A list which contains entries of Job, Integer. The job is the job chosen and
     * the Integer is the id of the processor where the job should execute
     */
    List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time);

    /**
     * Deletes the job of the active job list.
     * @param job the job to delete.
     */
    void deleteActiveJob(Job job);

    /**
     * Adds a job to the active job list which corresponds to the processor.
     * @param job the job to add to the active list job.
     */
    void addActiveJob(Job job);

    /**
     * Stops the execution of the job on the processor which corresponds to the id.
     * @param job the job to stop.
     * @param processorId the id of the processor.
     */
    void stopJobExecution(Job job, int processorId);

    /**
     * Execute the job on the processor which corresponds to the id
     * @param job the job to execute.
     * @param processorId the id of the processor.
     */
    void executeJob(Job job, int processorId);

    /**
     * Get the executing job of the processor.
     * @param processorId the id of the processor.
     * @return the job executing on this processor.
     */
    Job getExecutingJob(int processorId);



}
