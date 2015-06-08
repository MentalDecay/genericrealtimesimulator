package grts.core.simulator;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.events.IEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Scheduler {
    private final LinkedList<Job> activatedJobs =  new LinkedList<>();
    private  Job executingJob;
    private List<IEvent> eventsToAdd;
    private boolean over;
    private final IPriorityPolicy policy;
    private final HashMap<Job, Long> lastJobExecution = new HashMap<>();

    /**
     * Creates a new scheduler.
     * @param policy the priority policy which should be used by the scheduler.
     */
    public Scheduler(IPriorityPolicy policy) {
        this.policy = policy;
    }

    /**
     * Performs an event. The event handles itself and modifies the fields of this scheduler.
     * @param event the event to perform
     * @return A list of new event created by the event performed.
     */
    public List<IEvent> performEvent(IEvent event){
        resetEventsToAdd();
//        if(executingJob != null && event.getTime() != lastEventTime) {
//            executingJob.execute(event.getTime() - lastEventTime);
//        }
        event.doEvent();
        if(eventsToAdd == null){
            return Collections.emptyList();
        }
        return eventsToAdd;
    }

    /**
     * Checks if the simulation should end.
     * @return true if the simulation is over because of an event (StopSimulationEvent).
     */
    public boolean isOver() {
        return over;
    }

    /**
     * Ends the simulation. Only events should use this method.
     */
    public void endSimulation(){
        over = true;
    }

//    public Job getExecutingJob() {
//        return executingJob;
//    }

//    public void stopExecution() {
//        executingJob = null;
//    }

    /**
     * Resets the list of events which is returned by performEvent.
     */
    private void resetEventsToAdd(){
        eventsToAdd = new LinkedList<>();
    }

    /**
     * Adds an event to the list of events returned by performEvent.
     * @param event the events to be added.
     */
    public void addEvent(IEvent event){
        eventsToAdd.add(event);
    }


    /**
     * Deletes the job of the list of active jobs. Only events should use this method.
     * @param job the job to be deleted.
     */
    public void deleteActiveJob(Job job){
        activatedJobs.remove(job);
        lastJobExecution.remove(job);
    }

    /**
     * Adds a job to the list of active jobs. Only events should use this method.
     * @param job the job to be added
     */
    public void addActiveJob(Job job){
        activatedJobs.add(job);
    }

    /**
     * Deleted the current executing job of the scheduler. Only events should use this method.
     */
    public void stopJobExecution(){
        executingJob = null;
    }

    /**
     * The current executing job becomes the job in parameters. Only events should use this method.
     * @param job the job to execute
     */
    public void executeJob(Job job){
        executingJob = job;
    }

    /**
     * Get the current executing job.
     * @return the current executing job
     */
    public Job getExecutingJob() {
        return executingJob;
    }

    /**
     * Get the list of active jobs.
     * @return the list of active jobs.
     */
    public List<Job> getActivatedJobs() {
        return activatedJobs;
    }

    /**
     * Get the policy of the scheduler.
     * @return the policy of the scheduler.
     */
    public IPriorityPolicy getPolicy() {
        return policy;
    }


    /**
     * Get a map where each active job has its last beginning of execution time. Only events should use this method.
     * @return a HashMap<Job, Long> where the long is the last beginning of execution time of the job.
     */
    public HashMap<Job, Long> getLastJobExecution() {
        return lastJobExecution;
    }

    /**
     * Updates the last beginning of execution time of the job. Only events should use this method.
     * @param job the job to update
     * @param time the new time of the last execution time.
     */
    public void putLastJobExecution(Job job, long time) {
        lastJobExecution.put(job, time);
    }
}
