package grts.core.simulator;

import grts.core.schedulable.Job;

public class Processor {
    private final int id;
    private Job executingJob;

    /**
     * Creates a new processor.
     * @param id The id of the processor.
     */
    public Processor(int id) {
        this.id = id;
    }

    /**
     * Get the executing job of the processor.
     * @return The executing job of the processor.
     */
    public Job getExecutingJob() {
        return executingJob;
    }

    /**
     * Executes the job on this processor.
     * @param executingJob The job to execute on the processor.
     */
    public void executeJob(Job executingJob) {
        this.executingJob = executingJob;
    }

    /**
     * Stops the execution of the current job.
     */
    public void stopExecutionJob(){
        executingJob = null;
    }

    /**
     * Get the id of the processor.
     * @return The id of the processor.
     */
    public int getId() {
        return id;
    }
}
