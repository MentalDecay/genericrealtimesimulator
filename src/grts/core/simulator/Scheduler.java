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
    public Scheduler(IPriorityPolicy policy) {
        this.policy = policy;
    }

    List<IEvent> performEvent(IEvent event){
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

    public boolean isOver() {
        return over;
    }

    public void endSimulation(){
        over = true;
    }

//    public Job getExecutingJob() {
//        return executingJob;
//    }

//    public void stopExecution() {
//        executingJob = null;
//    }

    private void resetEventsToAdd(){
        eventsToAdd = new LinkedList<>();
    }

    public void addEvent(IEvent event){
        eventsToAdd.add(event);
    }



    public void deleteActiveJob(Job job){
        activatedJobs.remove(job);
        lastJobExecution.remove(job);
    }

    public void addActiveJob(Job job){
        activatedJobs.add(job);
    }

    public void stopJobExecution(){
        executingJob = null;
    }

    public void executeJob(Job job){
        executingJob = job;
    }

    public Job getExecutingJob() {
        return executingJob;
    }

    public LinkedList<Job> getActivatedJobs() {
        return activatedJobs;
    }

    public IPriorityPolicy getPolicy() {
        return policy;
    }

    public HashMap<Job, Long> getLastJobExecution() {
        return lastJobExecution;
    }

    public void putLastJobExecution(Job job, long time) {
        lastJobExecution.put(job, time);
    }
}
