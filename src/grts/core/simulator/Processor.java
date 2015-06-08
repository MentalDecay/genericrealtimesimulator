package grts.core.simulator;

import grts.core.schedulable.Job;

public class Processor {
    private final int id;
    private Job executingJob;

    public Processor(int id) {
        this.id = id;
    }

    public Job getExecutingJob() {
        return executingJob;
    }

    public void executeJob(Job executingJob) {
        this.executingJob = executingJob;
    }

    public void stopExecutionJob(){
        executingJob = null;
    }

    public int getId() {
        return id;
    }
}
