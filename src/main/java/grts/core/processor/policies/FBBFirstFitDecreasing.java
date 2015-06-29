package grts.core.processor.policies;

import grts.core.architecture.Architecture;
import grts.core.architecture.Processor;
import grts.core.priority.policies.DeadlineMonotonic;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Job;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FBBFirstFitDecreasing implements IProcessorPolicy {

    private final Architecture architecture;
    private final IPriorityPolicy priorityPolicy;
    private boolean schedulableTaskSet = true;
    private final HashMap<Integer, List<Job>> activatedJobsMap = new HashMap<>();
    private final HashMap<Integer, List<AbstractRecurrentTask>> processorIdToTask = new HashMap<>();
    private final HashMap<Schedulable, Integer> taskToProcessorId = new HashMap<>();
    private final boolean checkTwoConditions;

    public FBBFirstFitDecreasing(Architecture architecture, TaskSet taskSet) {
        if (!taskSet.isRecurrent()) {
            throw new IllegalArgumentException("Can't use this algorithm with a non recurrent task set");
        }
        this.architecture = architecture;
        priorityPolicy = new DeadlineMonotonic(taskSet);
        checkTwoConditions = !taskSet.isConstrained();
        init(taskSet);
        if (!schedulableTaskSet) {
            throw new IllegalArgumentException("Can't schedule this task set with this heuristic");
        }
    }


    private void init(TaskSet taskSet) {
        taskSet.stream()
                .sorted((o1, o2) -> Long.compare(o1.getDeadline(), o2.getDeadline()))
                .forEach(schedulable -> {

                    System.out.println("task : " + schedulable.getName());

                    boolean associatedWithProcessor = false;
                    for (Processor processor : architecture.getProcessors()) {
                        processorIdToTask.computeIfAbsent(processor.getId(), processorId -> new LinkedList<>());
                        if (firstCondition(schedulable, processorIdToTask.get(processor.getId()))) {
                            if (checkTwoConditions) {
                                if (secondCondition(schedulable, processorIdToTask.get(processor.getId()))) {
                                    processorIdToTask.get(processor.getId()).add((AbstractRecurrentTask) schedulable);
                                    taskToProcessorId.put(schedulable, processor.getId());
                                    associatedWithProcessor = true;
                                    break;
                                } else {
                                    System.out.println("fail");
                                }
                            } else {
                                processorIdToTask.get(processor.getId()).add((AbstractRecurrentTask) schedulable);
                                taskToProcessorId.put(schedulable, processor.getId());
                                associatedWithProcessor = true;
                                break;
                            }
                        }
                    }
                    if (!associatedWithProcessor) {
                        schedulableTaskSet = false;
                    }
                });
    }

    /**
     * Get the RBFStar of the schedulable task. The task should be an AbstractRecurrentTask.
     *
     * @param schedulable The AbstractRecurrentTask to test.
     * @param time        The timer when the RBFStart should compute.
     * @return The RBFStar (double).
     */
    private double RBFStar(Schedulable schedulable, long time) {
        AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
        return task.getWcet() + ((double) task.getWcet() / (double) task.getMinimumInterArrivalTime()) * time;
    }

    private boolean firstCondition(Schedulable schedulable, List<AbstractRecurrentTask> alreadyOnTheProcessor) {
        double sumRBF = alreadyOnTheProcessor.stream().mapToDouble(task -> RBFStar(task, schedulable.getDeadline())).sum();
        System.out.println("sumRBF : " + sumRBF);
        return schedulable.getDeadline() - sumRBF >= schedulable.getWcet();
    }

    private boolean secondCondition(Schedulable schedulable, List<AbstractRecurrentTask> alreadyOnTheProcessor) {
        double sumUtilization = alreadyOnTheProcessor.stream()
                .mapToDouble(value -> (double) value.getWcet() / (double) value.getMinimumInterArrivalTime())
                .sum();
        System.out.println("sumUtilization : " + sumUtilization);
        AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
        return (1 - sumUtilization) >= (double) task.getWcet() / (double) task.getMinimumInterArrivalTime();
    }

    @Override
    public Processor[] getProcessors() {
        return architecture.getProcessors();
    }


    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        List<AbstractMap.SimpleEntry<Job, Integer>> list = new LinkedList<>();
        for (Processor processor : architecture.getProcessors()) {
            List<Job> activatedJobs = activatedJobsMap.get(processor.getId());
            Job jobToExecute = priorityPolicy.choseJobToExecute(activatedJobs, time);
            if (jobToExecute == null) {
                continue;
            }
            list.add(new AbstractMap.SimpleEntry<>(jobToExecute, processor.getId()));
        }
        return list;
    }

    @Override
    public void deleteActiveJob(Job job) {
        Schedulable task = job.getTask();
        int processorId = taskToProcessorId.get(task);
        List<Job> activatedJobs = activatedJobsMap.get(processorId);
        if (!activatedJobs.remove(job)) {
            System.err.println("The job isn't in the activated job list.");
        }
    }

    @Override
    public void addActiveJob(Job job) {
        Schedulable task = job.getTask();
        int processorId = taskToProcessorId.get(task);
        activatedJobsMap.computeIfAbsent(processorId, id -> new LinkedList<>());
        activatedJobsMap.get(processorId).add(job);
    }

    @Override
    public void stopJobExecution(Job job, int processorId) {
        architecture.getProcessors()[processorId].stopExecutionJob();
    }

    @Override
    public void executeJob(Job job, int processorId) {
        architecture.getProcessors()[processorId].executeJob(job);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        return architecture.getProcessors()[processorId].getExecutingJob();
    }

    @Override
    public TaskSet initTaskSet(TaskSet taskSet) {
        return taskSet;
    }
}
