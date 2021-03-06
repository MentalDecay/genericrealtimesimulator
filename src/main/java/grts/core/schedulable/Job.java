package grts.core.schedulable;

import java.util.Objects;


public class Job {

    private final long activationTime;

    private final long deadlineTime;

    private final long jobId;

    private long executedTime = 0;

    private final long executionTime;

    private final AbstractRecurrentTask task;


    /**
     * Creates a new job associated to a task.
     * @param activationTime The activation time of the job.
     * @param deadlineTime The deadline time of the job.
     * @param jobId The id of the job.
     * @param executionTime The execution time of the job.
     * @param task the task which created the job.
     */
    public Job(long activationTime, long deadlineTime, long jobId, long executionTime, AbstractRecurrentTask task) {
        if(activationTime < 0 || executionTime <= 0 || deadlineTime <= 0 || jobId < 0){
            throw new IllegalArgumentException("Can't create a job with this parameters :" +
                    "\nactivationTime : " + activationTime +
                    "\nexecutionTime : " + executionTime +
                    "\ndeadlineTime : " + deadlineTime +
                    "\njobId : " + jobId
            );
        }
        this.activationTime = activationTime;
        this.deadlineTime = deadlineTime;
        this.jobId = jobId;
        this.executionTime = executionTime;
        this.task = Objects.requireNonNull(task);
    }

    /**
     * Get the job ID.
     * @return the job ID
     */
    public long getJobId(){
        return jobId;
    }

    /**
     * Get the remaining time of the job.
     * @return the remaining time of the job
     */
    public long getRemainingTime(){
        return executionTime - executedTime;
    }


    /**
     * Execute the job during one unit time. If the job can't execute (because no remaining execution time) an IllegalStateException is raised.
     */
    public void execute(){
        executedTime++;
        if(executedTime > executionTime){
            throw new IllegalStateException("The job exceeded its execution time : " + getJobId() + " " + getTask().getName());
        }
    }

    /**
     * Executes the job during the time passed in parameter. If the job exceeds its execution time an IllegalStateException is raised.
     * @param time the time in the course of which the job should execute
     */
    public void execute(long time){
        if(time < 0){
            throw new IllegalArgumentException("Can't execute a job during <= 0 units of time.");
        }
        executedTime += time;
        if(executedTime > executionTime){
            throw new IllegalStateException("The job exceeded its execution time : " + getJobId() + " " + getTask().getName());
        }
    }

    /**
     * Get the recurrent task attached to this job.
     * @return AbstractRecurrentTask
     */
    public AbstractRecurrentTask getTask(){
        return task;
    }

    /**
     * Check if the deadline missed at the time.
     * @param time when the method should check the deadline.
     * @return true if the deadline missed
     */
    public boolean deadlineMissed(long time){
        return !(time >= activationTime && time <= deadlineTime);
    }

    /**
     * Get the deadline time
     * @return the deadline time
     */
    public long getDeadlineTime() {
        return deadlineTime;
    }

    /**
     * Get the period of the task which created this job.
     * @return the period of the task which created this job
     */
    public long getTaskInterArrivalTime() {
        return task.getMinimumInterArrivalTime();
    }

    /**
     * Compute the laxity of the job.
     * @param time the time when the laxity should be computed
     * @return the laxity of the job at this time
     */
    public long getLaxity(long time) {
        return deadlineTime - getRemainingTime() - time;
    }

    /**
     * Get the deadline of the task which created this job.
     * @return the deadline of the task which created this job
     */
    public long getDeadlineTask(){
        return getTask().getDeadline();
    }

    /**
     * Get the activation time of this job.
     * @return the activation time of this job
     */
    public long getActivationTime() {
        return activationTime;
    }

    @Override
    public String toString() {
        return "Job (" + jobId + ") from " + getTask().getName() + " (" + getActivationTime() + ", " + getDeadlineTime() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Job)){
            return false;
        }
        Job job = (Job) obj;
        return activationTime == job.activationTime && deadlineTime == job.deadlineTime && jobId == job.jobId && executionTime == job.executionTime
                && task.equals(job.task);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(activationTime);
        hash = hash * 31 + Float.floatToIntBits(deadlineTime);
        hash = hash * 31 + Float.floatToIntBits(jobId);
        hash = hash * 31 + Float.floatToIntBits(executionTime);
        hash = hash * 31 + task.hashCode();
        return hash;
    }

    /**
     * Get the execution time of the job.
     * @return The execution time of the job.
     */
    public long getExecutionTime() {
        return executionTime;
    }
}
