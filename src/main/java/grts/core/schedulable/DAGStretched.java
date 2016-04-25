package grts.core.schedulable;

import java.util.*;

public class DAGStretched extends AbstractRecurrentTask implements Schedulable{
    private final int subtasksNumber;
    private final HashMap<Integer, HashSet<Integer>> relations;
    private final long[] substasksCosts;
    private StretchedJob masterThread;
    private List<StretchedJob> otherJobs = new ArrayList<>();
    private final List<Job> nextJobs = new LinkedList<>();

    private class StretchedJob{
        private final long offset;
        private final long deadline;
        private long wcet;

        private StretchedJob(long offset, long deadline, long wcet) {
            this.offset = offset;
            this.deadline = deadline;
            this.wcet = wcet;
        }

        private void addCost(long cost){
            wcet += cost;
        }

        //Maybe not enough parameters in the equals.
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof StretchedJob)){
                return false;
            }
            StretchedJob stretchedJob = (StretchedJob) obj;
            return offset == stretchedJob.offset &&
                    deadline == stretchedJob.deadline &&
                    wcet == stretchedJob.deadline;
        }
    }

    private class Segment {
        private final long from;
        private final long to;
        private final ArrayList<Integer> substasksContained = new ArrayList<>();

        private Segment(long from, long to) {
            this.from = from;
            this.to = to;
        }
    }


    private class MTS{
        private Segment[] segments;
        private long longestPath;

        private MTS(HashMap<Integer, HashSet<Integer>> relations) {
            HashMap<Integer, HashSet<Integer>> reversedRelations = new HashMap<>();
            for(int i = 0; i < substasksCosts.length; i++){
                reversedRelations.put(i, new HashSet<>());
            }
            relations.forEach((integer, integers) -> integers.forEach(integer1 -> reversedRelations.get(integer1).add(integer)));
            computeSegments(reversedRelations);
        }

        private void computeSegments(HashMap<Integer, HashSet<Integer>> reversedRelations){
            boolean[] subtaskWithEvent = new boolean[substasksCosts.length];
            long[] subtaskStartedAt = new long[substasksCosts.length];
            long[] subtaskEndedAt = new long[substasksCosts.length];
            TreeMap<Long, List<Integer>> events = new TreeMap<>();
            long time = 0;
            do{
                if(!events.isEmpty()){
                    Map.Entry<Long, List<Integer>> entry = events.pollFirstEntry();
                    time = entry.getKey();
                    entry.getValue().forEach(integer -> {
                        reversedRelations.remove(integer);
                        subtaskEndedAt[integer] = entry.getKey();
                    });
                    reversedRelations.entrySet().forEach(integerHashSetEntry -> entry.getValue().forEach(integer -> integerHashSetEntry.getValue().remove(integer)));
                }
                long finalTime = time;
                reversedRelations.forEach((integer, integers) -> {
                    if(integers.isEmpty() && !subtaskWithEvent[integer]){
                        subtaskWithEvent[integer] = true;
                        long eventTime = finalTime + substasksCosts[integer];
                        events.putIfAbsent(eventTime, new LinkedList<>());
                        events.get(eventTime).add(integer);
                        subtaskStartedAt[integer] = finalTime;
                    }
                });
            }while(!events.isEmpty());
            completeSegments(subtaskStartedAt, subtaskEndedAt);
        }

        private void completeSegments(long[] subtaskStartedAt, long[] subtaskEndedAt){
            segments = new Segment[(int) Arrays.stream(subtaskEndedAt).distinct().count()];
            List<Long> list = new LinkedList<>();
            list.add(0L);
            Arrays.stream(subtaskEndedAt).distinct().sorted().forEach(value -> {
                list.add(value);
                list.add(value);
            });
            for (int i = 0; i < segments.length; i++) {
                long from = list.remove(0);
                long to = list.remove(0);
                segments[i] = new Segment(from, to);
            }
            for (Segment segment : segments) {
                for(int i = 0; i < subtaskStartedAt.length; i++){
                    long from = subtaskStartedAt[i];
                    long to = subtaskEndedAt[i];
                    if(((from >= segment.from && from < segment.to) || (to > segment.from && to <= segment.to))
                            || (from <= segment.from && to >= segment.to)){
                        segment.substasksContained.add(i);
                    }
                }
            }
            longestPath = segments[segments.length - 1].to;
        }
    }


    public DAGStretched(long minimumInterArrivalTime, long wcet, long deadline, long offset, String name, int subtasksNumber,
                        HashMap<Integer, HashSet<Integer>> relations, long[] subtasksCosts) {
        super(minimumInterArrivalTime, wcet, deadline, offset, name);
        this.subtasksNumber = subtasksNumber;
        this.relations = new HashMap<>(relations);
        this.substasksCosts = new long[subtasksCosts.length];
        System.arraycopy(subtasksCosts, 0, this.substasksCosts, 0, subtasksCosts.length);
        applyDAGStretchingAlgorithm();
        nextJobs.add(createJob(masterThread.offset, masterThread.deadline + offset, masterThread.wcet));
        otherJobs.forEach(stretchedJob ->
                nextJobs.add(createJob(stretchedJob.offset, stretchedJob.deadline + offset + stretchedJob.offset, stretchedJob.wcet)));
    }

    public DAGStretched(Map<String, Object> map) {
        super(map);
        this.subtasksNumber = (int) map.get("subtasksNumber");
        this.relations = (HashMap<Integer, HashSet<Integer>>) map.get("relations");
        long[] array = (long[]) map.get("subtaskCosts");
        this.substasksCosts = new long[array.length];
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
    public List<Job> getRealNextJob(long time) {
        if(nextJobs.get(0) != null && time < nextJobs.get(0).getActivationTime()){
            return nextJobs;
        }
        else{
            List<Job> tmp = new LinkedList<>();
            long masterActivationTime = nextJobs.remove(0).getActivationTime() + getNextInterArrivalTime();
            tmp.add(createJob(masterActivationTime, masterActivationTime + getDeadline(), masterThread.wcet));
            int size = nextJobs.size();
            for (int i = 0; i < size; i++) {
                long activationTime = nextJobs.remove(0).getActivationTime() + getNextInterArrivalTime();
                tmp.add(createJob(activationTime, activationTime + otherJobs.get(i).deadline, otherJobs.get(i).wcet));
            }
            nextJobs.addAll(tmp);
            return nextJobs;
        }
    }

    @Override
    public Schedulable copy() {
        return new DAGStretched(getMinimumInterArrivalTime(), getWcet(), getDeadline(), getOffset(), getName(),
                subtasksNumber, relations, substasksCosts);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DAGStretched)){
            return false;
        }
        DAGStretched dagStretched = (DAGStretched) obj;
        return getMinimumInterArrivalTime() == dagStretched.getMinimumInterArrivalTime() &&
                getWcet()  == dagStretched.getWcet() &&
                getDeadline() == dagStretched.getDeadline() &&
                getOffset() == dagStretched.getOffset() &&
                getName().equals(dagStretched.getName()) &&
                getSubtasksNumber() == getSubtasksNumber() &&
                relations.equals(dagStretched.relations) &&
                Arrays.equals(substasksCosts, dagStretched.substasksCosts) &&
                masterThread.equals(dagStretched.masterThread) &&
                otherJobs.equals(dagStretched.otherJobs);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Float.floatToIntBits(getMinimumInterArrivalTime());
        hash = hash * 31 + Float.floatToIntBits(getWcet());
        hash = hash * 31 + Float.floatToIntBits(getDeadline());
        hash = hash * 31 + Float.floatToIntBits(getOffset());
        hash = hash * 31 + getName().hashCode();
        hash = hash * 31 + Float.floatToIntBits(getSubtasksNumber());
        hash = hash * 31 + relations.hashCode();
        hash = hash * 31 + Arrays.hashCode(substasksCosts);
        hash = hash * 31 + masterThread.hashCode();
        hash = hash * 31 + otherJobs.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("otherTasks : \n");
        otherJobs.forEach(stretchedJob -> {
            stringBuilder.append("\t\t\t offset : ").append(stretchedJob.offset).append("\n");
            stringBuilder.append("\t\t\t wcet : ").append(stretchedJob.wcet).append("\n");
            stringBuilder.append("\t\t\t deadline : ").append(stretchedJob.deadline).append("\n\n");
        });
        return "Stretched Graph : \n" +
                "period : " + getPeriod() + "\n" +
                "wcet : " + getWcet() + "\n" +
                "deadline : " + getDeadline() + "\n" +
                "offset : " + getOffset() + "\n" +
                "name : " + getName() + "\n" +
                "subtasksNumber : " + subtasksNumber + "\n" +
                "masterThread : " + "\n" +
                "\t\t\t offset : " + masterThread.offset + "\n" +
                "\t\t\t wcet : " + masterThread.wcet + "\n" +
                "\t\t\t deadline : " + masterThread.deadline + "\n" +
                stringBuilder.toString();
    }

    public int getSubtasksNumber() {
        return subtasksNumber;
    }

    private void applyDAGStretchingAlgorithm(){
        if(getWcet() == getPeriod()){
            masterThread = new StretchedJob(0, getDeadline(), getWcet());
        }
        else{
            masterThread = new StretchedJob(0, getDeadline(), 0);
            long totalCost = 0;
            if(getWcet() < getPeriod()){
                for(long cost : substasksCosts){
                    totalCost += cost;
                }
                otherJobs.add(new StretchedJob(0, getDeadline(), totalCost));
                //totalCost = getWcet ?
                //Why not masterThread ?
            }
            else{
                MTS mts = new MTS(relations);
                double unitFactor = ((double)getDeadline() - (double) mts.longestPath) / ((double) getWcet() - (double) mts.longestPath);
                long previousDeadline = 0;
                for (Segment segment : mts.segments) {
                    double segmentFactor = unitFactor * ((double) segment.substasksContained.size() - 1);
                    int nbOtherThreads = segment.substasksContained.size() - (int) Math.floor(segmentFactor) - 1;
                    if(nbOtherThreads == 0){
                        masterThread.addCost(segment.to - segment.from);
                    }
                    else {
                        int k;
                        for (k = 0; k < nbOtherThreads; k++) {
                            masterThread.addCost(segment.to - segment.from);
                        }
                        long wcet;
                        long offset;
                        long deadline;
                        long addedJobTotalCost = segment.to - segment.from;
                        masterThread.addCost((long) ((segmentFactor - Math.floor(segmentFactor)) * addedJobTotalCost));
                        wcet = (long) ((1 + Math.floor(segmentFactor) - segmentFactor) * addedJobTotalCost);
                        offset = getOffset() + previousDeadline;
                        deadline = (long) ((1 + Math.floor(segmentFactor)) * addedJobTotalCost);
                        otherJobs.add(new StretchedJob(offset, deadline, wcet));
                        k++;
                        for (; k < segment.substasksContained.size(); k++) {
                            wcet = segment.to - segment.from;
                            offset = getOffset() + previousDeadline;
                            deadline = (long) ((1 + segmentFactor) * segment.to - segment.from);
                            otherJobs.add(new StretchedJob(offset, deadline, wcet));
                        }
                    }
                    previousDeadline = masterThread.wcet + getOffset();
                }
            }
        }
    }
}
