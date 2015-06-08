package grts.core.schedulable;

public interface ITask {

    /**
     * Get the Worst Case Execution Time of the task.
     * @return WCET of the task
     */
    long getWcet();


    /**
     * Get the deadline of the task.
     * @return deadline of the task
     */
    long getDeadline();


    /**
     * Get the name of the task
     * @return String name
     */
    String getName();

    /**
     * Get the offset of the task.
     * @return the offset of the task
     */
    long getOffset();

    /**
     * Get the time of the next activation of a job. Returns the current time if the next activation is at this time.
     * Also updates the next job if needed.
     * @param time when the task should get the time of the next activation of a job.
     * @return the time of the next activation of a job
     */
    long getNextActivationTime(long time);

    /**
     * Get the next job of the task. Should only be used after checking if time matches with the next activation time to avoid jobs redundancy.
     * @return the next job of the task
     */
    Job getNextJob(long time);

    /**
     * Get the next job according to the time. If the time is equal or superior to the next job returned, a new job is created.
     * @param time when the task should return a new job
     * @return the new Job created by the task or previously created if the time is inferior to the activation of the next job
     */
    Job getRealNextJob(long time);

    /**
     * Get the first job of the task. Should be used only one time.
     * @return the first Job of the task.
     */
    Job getFirstJob();

}
