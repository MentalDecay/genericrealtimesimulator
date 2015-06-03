package GRTS.core;

import GRTS.core.schedulable.Job;
import GRTS.core.priority.policies.IPriorityPolicy;
import GRTS.core.taskset.ITaskSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Scheduler {

    private final ITaskSet taskSet;
    private final IPriorityPolicy policy;
    private final List<Job> activeJobs = new LinkedList<>();
    private Job executingJob;


    public Scheduler(ITaskSet taskSet, IPriorityPolicy policy) {
        this.taskSet = Objects.requireNonNull(taskSet);
        this.policy = Objects.requireNonNull(policy);
    }

    /**
     * Schedule tasks for the max time given.
     * @param maxTime the number of units of maxTime the scheduler should do.
     */
    public void schedule(long maxTime){
        long time = 0;
        while(time < maxTime){
            System.out.println("Time : " + time);
            activateJobs(time);
            Job jobToExecute = policy.choseJobToExecute(activeJobs, time);


            if(jobToExecute != null){
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // Disgusting displays to move
                if (executingJob == null) {
                    System.out.println("New job executing : " + jobToExecute.getJobId() +
                            " from : " + jobToExecute.getTask().getName());
                } else if (jobToExecute != executingJob) {
                    System.out.println("Job (" + jobToExecute.getJobId() + ") from " + jobToExecute.getTask().getName() +
                            " is preempting job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName());
                    System.out.println("Job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() +
                            " stops. " + executingJob.getRemainingTime() + " unit(s) of time remaining.");
                } else {
                    System.out.println("Job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() +
                            " continue its execution. " + executingJob.getRemainingTime() + " unit(s) of time remaining");
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // End disgusting displays
                executeJob(jobToExecute);
            }

            if(checkDeadlineMissed(time)){
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // Disgusting displays to move
                taskSet.getRecurrentTasks().forEach(System.out::println);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // End disgusting displays
                return;
            }
            time++;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Disgusting displays to move
        taskSet.getRecurrentTasks().forEach(System.out::println);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // End disgusting displays
    }

    /**
     * Executes the job for one time unit and deletes it from the active jobs list if the job is over.
     * @param job the job to execute from active jobs list.
     */
    private void executeJob(Job job){
        executingJob = job;
        executingJob.execute();
        if(executingJob.getRemainingTime() == 0){
            System.out.println("Job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() + " ended its execution.");
            activeJobs.remove(executingJob);
            executingJob = null;
        }
    }

    /**
     * Activates jobs at the time date
     * @param time time when the job is activated
     */
    private void activateJobs(long time) {
        taskSet.getRecurrentTasks().stream().filter(task -> time == task.getNextActivationTime(time)).forEach(task -> {
            Job jobToAdd = task.getNextJob(time);
            System.out.println("activating job : " + jobToAdd.getJobId() + " from " + jobToAdd.getTask().getName());
            activeJobs.add(jobToAdd);
        });
    }

    /**
     * Checks for each job the active jobs list if a deadline is missed at the time.
     * @param time when the method should check the deadline.
     * @return true if a deadline missed
     */
    private boolean checkDeadlineMissed(long time) {
        for(Job job : activeJobs){
            if(job.deadlineMissed(time)){
                System.out.println("Job (" + job.getJobId() + ") from " + job.getTask().getName() + " just missed its deadline at " + time);
                return true;
            }
        }
        return false;
    }


}
