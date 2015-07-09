package grts.core.processor.policies;

import grts.core.architecture.Architecture;
import grts.core.architecture.Processor;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Job;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import grts.core.tests.SchedulabilityTestFactory;

import java.util.*;
import java.util.function.Predicate;

public class FirstFitDecreasingUtilizationPolicy implements IProcessorPolicy {

    private final Architecture architecture;
    private final IPriorityPolicy priorityPolicy;
    private final SchedulabilityTestFactory schedulabilityTestFactory;
    private final HashMap<Schedulable, Integer> taskToProcessorId = new HashMap<>();
    private final HashMap<Integer, List<Schedulable>> processorIdToTask = new HashMap<>();
    private boolean schedulableTaskSet = true;
    private final HashMap<Integer, List<Job>> activatedJobsMap = new HashMap<>();

    /**
     * Creates a new First Fit Decreasing Utilization Policy. The initialization may take time because of the
     * computation of where each Schedulable should execute.
     *
     * @param architecture   The architecture used with this Processor Policy.
     * @param priorityPolicy The priority policy used with this Processor Policy.
     * @param taskSet        The TaskSet the Processor Policy should schedule.
     */
    public FirstFitDecreasingUtilizationPolicy(Architecture architecture, IPriorityPolicy priorityPolicy, TaskSet taskSet) {
        this.architecture = architecture;
        this.priorityPolicy = priorityPolicy;
        schedulabilityTestFactory = new SchedulabilityTestFactory(architecture);
        init(taskSet);
        if (!schedulableTaskSet) {
            throw new IllegalArgumentException("Can't schedule this task set with this heuristic");
        }
    }

    private void init(TaskSet taskSet) {
        Predicate<TaskSet> taskSetPredicate = schedulabilityTestFactory.getTaskSetTest(priorityPolicy.getPolicyName());
        taskSet.stream().sorted((o1, o2) -> {
            if (!(o1 instanceof AbstractRecurrentTask) || !(o2 instanceof AbstractRecurrentTask)) {
                throw new IllegalArgumentException("Can't use the First Fit Decreasing Utilization on a non recurrent taskSet");
            }
            AbstractRecurrentTask t1 = (AbstractRecurrentTask) o1;
            AbstractRecurrentTask t2 = (AbstractRecurrentTask) o2;
            double utilizationT1 = (double) t1.getWcet() / (double) t1.getMinimumInterArrivalTime();
            double utilizationT2 = (double) t2.getWcet() / (double) t2.getMinimumInterArrivalTime();
            return -Double.compare(utilizationT1, utilizationT2);
        }).forEach(schedulable -> {
            boolean associatedWithProcessor = false;
            for (Processor processor : architecture.getProcessors()) {
                processorIdToTask.computeIfAbsent(processor.getId(), integer -> new LinkedList<>());
                List<Schedulable> tasks = processorIdToTask.get(processor.getId());
                List<Schedulable> copyTasks = new LinkedList<>();
                copyTasks.addAll(tasks);
                copyTasks.add(schedulable);
                TaskSet taskSetToTest = new TaskSet(copyTasks);
                if (taskSetPredicate.test(taskSetToTest)) {
                    taskToProcessorId.put(schedulable, processor.getId());
                    processorIdToTask.get(processor.getId()).add(schedulable);
                    associatedWithProcessor = true;
                    break;
                }
            }
            if (!associatedWithProcessor) {
                schedulableTaskSet = false;
            }
        });
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
//        System.out.println("delete");
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
