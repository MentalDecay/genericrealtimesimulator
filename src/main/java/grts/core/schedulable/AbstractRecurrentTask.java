package grts.core.schedulable;

import java.util.Map;


public abstract class AbstractRecurrentTask implements Schedulable {
    private final long minimumInterArrivalTime;
    private final long wcet;
    private final long deadline;
    private final long offset;
    private long nbJob = 0;
    private final String name;
    // This field needs to be seen by the subclasses
    //protected long nextActivationTime;
    private Job nextJob;
    private boolean newJob = false;

    AbstractRecurrentTask(long minimumInterArrivalTime, long wcet, long deadline, long offset, String name) {
        if(minimumInterArrivalTime <= 1 || wcet <= 0 || deadline <= 0 || offset < 0 || name.isEmpty()){
            throw new IllegalArgumentException("Can't create a task with this parameters :" +
                    "\ninterArrivalTime : " + minimumInterArrivalTime +
                    "\nwcet : " + wcet +
                    "\ndeadline : " + deadline +
                    "\noffset : " + offset +
                    "\nname : " + name
            );
        }
        this.minimumInterArrivalTime = minimumInterArrivalTime;
        this.wcet = wcet;
        this.deadline = deadline;
        this.offset = offset;
        this.name = name;
        nextJob = new Job(offset, offset + deadline, 0, wcet, this);
        //this.nextActivationTime = offset;
    }

    AbstractRecurrentTask(Map<String, Object> map){
        Long minimumInterArrivalTime, wcet, deadline, offset;
        minimumInterArrivalTime = (Long) map.get("minimumInterArrivalTime");
        wcet = (Long) map.get("wcet");
        deadline = (Long) map.get("deadline");
        offset = (Long) map.get("offset");
        String name = (String) map.get("name");
        if(minimumInterArrivalTime == null || wcet == null || deadline == null || offset == null || name == null){
            throw new IllegalArgumentException("Can't create a new Recurrent Task with this parameters : " + map);
        }
        if(minimumInterArrivalTime <= 1 || wcet <= 0 || deadline <= 0 || offset < 0 || name.isEmpty()){
            throw new IllegalArgumentException("Can't create a task with this parameters :" +
                    "\ninterArrivalTime : " + minimumInterArrivalTime +
                    "\nwcet : " + wcet +
                    "\ndeadline : " + deadline +
                    "\noffset : " + offset +
                    "\nname : " + name
            );
        }
        this.minimumInterArrivalTime = minimumInterArrivalTime;
        this.wcet = wcet;
        this.deadline = deadline;
        this.offset = offset;
        this.name = name;
        nextJob = new Job(offset, offset + deadline, 0, wcet, this);

    }

    @Override
    public long getWcet() {
        return wcet;
    }

    public long getMinimumInterArrivalTime() {
        return minimumInterArrivalTime;
    }

    @Override
    public long getDeadline() {
        return deadline;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public long getNextActivationTime(long time){
        if(time < getOffset()){
            return nextJob.getActivationTime();
        }
        if(time == nextJob.getActivationTime()){
            newJob = true;
            return nextJob.getActivationTime();
        }
        else if(newJob){
            long activationTime = nextJob.getActivationTime() + getNextInterArrivalTime();
            nextJob = createJob(activationTime, activationTime + deadline, wcet);
            newJob = false;
        }
        return nextJob.getActivationTime();
    }

    @Override
    public Job getNextJob(long time) {
        return nextJob;
    }

    /**
     * Creates a new job with this parameters. The job id is the actual number of jobs created by this task.
     * @param activeTime the time of activation of the job
     * @param deadlineTime the time of deadline of the job
     * @param wcet the wcet of the job
     * @return A new job associated with this task.
     */
    protected Job createJob(long activeTime, long deadlineTime, long wcet){
        return new Job(activeTime, deadlineTime, nbJob++, wcet, this);
    }

    /**
     * Get the inter arrival time of the current job.
     * @return the inter arrival time of the current job
     */
    protected abstract long getNextInterArrivalTime();

    /**
     * Get the number of jobs created by this task.
     * @return the number of jobs created by this task
     */
    public long getNbJob() {
        return nbJob;
    }

    /**
     * Get the next job according to the time.
     * @param time the time from which the next job is requested
     * @return the next job which has to be activated after time
     */
    @Override
    public abstract Job getRealNextJob(long time);

    @Override
    public Job getFirstJob() {
        return createJob(offset, deadline + offset, wcet);
    }
}
