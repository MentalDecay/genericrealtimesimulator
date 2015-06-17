package grts.core.simulator;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.events.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Scheduler {
    private List<Event> eventsToAdd;
    private boolean over;
    private final IPriorityPolicy priorityPolicy;
    private final IProcessorPolicy processorPolicy;
    private final HashMap<Job, Long> lastJobExecution = new HashMap<>();

    /**
     * Creates a new scheduler.
     * @param priorityPolicy the priority policy which should be used by the scheduler.
     * @param processorPolicy the processor policy which should be used by the scheduler.
     */
    public Scheduler(IPriorityPolicy priorityPolicy, IProcessorPolicy processorPolicy) {
        this.priorityPolicy = priorityPolicy;
        this.processorPolicy = processorPolicy;
    }

    /**
     * Performs an event. The event handles itself and modifies the fields of this scheduler.
     * @param event the event to perform
     * @return A list of new event created by the event performed.
     */
    public List<Event> performEvent(Event event){
        resetEventsToAdd();
        event.handle();
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
    public void addEvent(Event event){
        eventsToAdd.add(event);
    }


    /**
     * Deletes the job of the list of active jobs. Only events should use this method.
     * @param job the job to be deleted.
     */
    public void deleteActiveJob(Job job){
        processorPolicy.deleteActiveJob(job);
        lastJobExecution.remove(job);
    }

    /**
     * Adds a job to the list of active jobs. Only events should use this method.
     * @param job the job to be added
     */
    public void addActiveJob(Job job){
        processorPolicy.addActiveJob(job);
    }

    /**
     * Deleted the current executing job of the scheduler. Only events should use this method.
     */
    public void stopJobExecution(Job job, int processorId){
        processorPolicy.stopJobExecution(job, processorId);
    }

    /**
     * The current executing job becomes the job in parameters. Only events should use this method.
     * @param job the job to execute
     */
    public void executeJob(Job job, int processorId){
        processorPolicy.executeJob(job, processorId);
    }

    /**
     * Get the current executing job.
     * @return the current executing job
     */
    public Job getExecutingJob(int processorId) {
        return processorPolicy.getExecutingJob(processorId);
    }


    /**
     * Get the priorityPolicy of the scheduler.
     * @return the priorityPolicy of the scheduler.
     */
    public IPriorityPolicy getPriorityPolicy() {
        return priorityPolicy;
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

    /**
     * Get the processor policy of the scheduler.
     * @return The processor policy of the scheduler.
     */
    public IProcessorPolicy getProcessorPolicy() {
        return processorPolicy;
    }
}
