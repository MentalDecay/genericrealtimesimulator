package grts.core.schedulable;

import java.util.HashMap;
import java.util.Map;

public class DAGStretched extends AbstractRecurrentTask implements Schedulable{
    private final int subtasksNumber;
    private final HashMap<Integer, Integer> relations;
    private final int[] substasksCosts;


    public DAGStretched(long minimumInterArrivalTime, long wcet, long deadline, long offset, String name, int subtasksNumber,
                        HashMap<Integer, Integer> relations, int[] substasksCosts) {
        super(minimumInterArrivalTime, wcet, deadline, offset, name);
        this.subtasksNumber = subtasksNumber;
        this.relations = new HashMap<>(relations);
        this.substasksCosts = new int[substasksCosts.length];
        System.arraycopy(substasksCosts, 0, this.substasksCosts, 0, substasksCosts.length);
    }

    public DAGStretched(Map<String, Object> map) {
        super(map);
        this.subtasksNumber = (int) map.get("subtasksNumber");
        this.relations = (HashMap<Integer, Integer>) map.get("relations");
        int[] array = (int[]) map.get("subtaskCosts");
        this.substasksCosts = new int[array.length];
        System.arraycopy(array, 0, substasksCosts, 0, array.length);
    }

    public long getPeriod(){
        return getMinimumInterArrivalTime();
    }

    @Override
    protected long getNextInterArrivalTime() {
        return getMinimumInterArrivalTime();
    }

    @Override
    public Job getRealNextJob(long time) {
        return null;
        //TODO
    }

    @Override
    public Schedulable copy() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DAGStretched)){
            return false;
        }
        DAGStretched task = (DAGStretched) obj;
        return getMinimumInterArrivalTime() == task.getMinimumInterArrivalTime() &&
                getWcet()  == task.getWcet() &&
                getDeadline() == task.getDeadline() &&
                getOffset() == task.getOffset() &&
                getName().equals(task.getName()) &&
                getSubtasksNumber() == getSubtasksNumber();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
        hash = hash * 31 + getSubtasksNumber();
        return hash;
    }

    @Override
    public String toString() {
//        long period, long wcet, long deadline, long offset, String name
        return "Graph Task : \n" +
                "period : " + getPeriod() + "\n" +
                "wcet : " + getWcet() + "\n" +
                "deadline : " + getDeadline() + "\n" +
                "offset : " + getOffset() + "\n" +
                "name : " + getName() + "\n" +
                "subtasksNumber : " + getOffset();
    }

    public int getSubtasksNumber() {
        return subtasksNumber;
    }
}
