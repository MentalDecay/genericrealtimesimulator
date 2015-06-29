package grts.core.schedulable;

import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.RandomStream;

import java.util.*;

public class SporadicTask extends AbstractRecurrentTask implements Schedulable {

    // Contains all the inter arrival times of each job (0 = first job, 1 = second job ...)
    private final ArrayList<Long> interArrivalTimes = new ArrayList<>();
    private final RandomStream randomStream;
    private Job realNextJob;

    /**
     * Creates a new sporadic task.
     * @param minimumInterArrivalTime The minimum inter arrival time between two jobs.
     * @param wcet The Worst Case Execution TIme of the task.
     * @param deadline The deadline of the task.
     * @param offset The offset of the task.
     * @param name The name of the task.
     */
    public SporadicTask(long minimumInterArrivalTime, long wcet, long deadline, long offset, String name) {
        super(minimumInterArrivalTime, wcet, deadline, offset, name);
        initRandom();
        randomStream = new LFSR113();
        realNextJob = createJob(offset, offset + deadline, wcet);
    }

    /**
     * Creates a new sporadic task.
     * @param map a map of String and Object which contains the minimumInterArrivalTime (long), the wcet (long), the deadline (long), the offset (long) and the name (String).
     */
    public SporadicTask(Map<String, Object> map){
        super(map);
        initRandom();
        randomStream = new LFSR113();
        realNextJob = createJob(getOffset(), getOffset() + getDeadline(), getWcet());
    }

    private static void initRandom(){
        Random rand = new Random();
        int [] tab = new int[4];
        tab[0] = rand.nextInt(Integer.MAX_VALUE - 2) + 2;
        tab[1] = rand.nextInt(Integer.MAX_VALUE - 8) + 8;
        tab[2] = rand.nextInt(Integer.MAX_VALUE - 16) + 16;
        tab[3] = rand.nextInt(Integer.MAX_VALUE - 128) + 128;
        LFSR113.setPackageSeed(tab);
    }

    /**
     * The inter arrival time of a sporadic task. It can change between each job. This method uses a an exponential distribution to compute the inter arrival time.
     * Approximately 45% of the tasks have the same inter arrival time as the period, the other are greater (10 times was the maximum during tests, but could be greater).
     * @return the next arrival time
     */
    @Override
    protected long getNextInterArrivalTime() {
        if(interArrivalTimes.size() < getNbJob()){
            interArrivalTimes.add((long) (ExponentialGen.nextDouble(randomStream, 6) * getMinimumInterArrivalTime()) + getMinimumInterArrivalTime());
        }
        return interArrivalTimes.get((int)getNbJob()-1);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Sporadic task : " + getName() + "\n");
        for(int i = 0; i < interArrivalTimes.size(); i++){
            str.append("inter arrival time for ").append(i).append(" : ").append(interArrivalTimes.get(i)).append("\n");
        }
        return str.toString();
    }

    @Override
    public Job getRealNextJob(long time) {
        if(realNextJob != null && time < realNextJob.getActivationTime()){
            return realNextJob;
        }
        else{
            long activationTime = (long) (ExponentialGen.nextDouble(randomStream, 6) * getMinimumInterArrivalTime()) + getMinimumInterArrivalTime() + realNextJob.getActivationTime();
            realNextJob = createJob(activationTime, activationTime + getDeadline(), getWcet());
            return realNextJob;
        }
    }

    @Override
    public Schedulable copy() {
        return new SporadicTask(getMinimumInterArrivalTime(), getWcet(), getDeadline(), getOffset(), getName());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SporadicTask)){
            return false;
        }
        SporadicTask task = (SporadicTask) obj;
        return getMinimumInterArrivalTime() == task.getMinimumInterArrivalTime() &&
                getWcet()  == task.getWcet() &&
                getDeadline() == task.getDeadline() &&
                getOffset() == task.getOffset() &&
                getName().equals(task.getName());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
        return hash;
    }
}
