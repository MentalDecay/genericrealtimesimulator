package grts.core.schedulable;

import java.util.*;

public class DAGStretched extends AbstractRecurrentTask implements Schedulable{
    private final int subtasksNumber;
    private final HashMap<Integer, HashSet<Integer>> relations;
    private final long[] substasksCosts;

    private class StretchedJob{
        private long offset;
        private long deadline;
        private long wcet;

        private StretchedJob(long offset, long deadline, long wcet) {
            this.offset = offset;
            this.deadline = deadline;
            this.wcet = wcet;
        }

        private void addCost(long cost){
            wcet += cost;
        }

        private void addDeadline(long deadline){
            this.deadline += deadline;
        }
    }

    private StretchedJob masterThread;
    private List<StretchedJob> otherJobs = new LinkedList<>();

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
//            computeSegments();
            System.out.println("reversedRelations : " + reversedRelations);
            computeSegments(reversedRelations);
        }

        private void computeSegments(HashMap<Integer, HashSet<Integer>> reversedRelations){
            boolean[] subtaskWithEvent = new boolean[substasksCosts.length];
//            HashMap<Integer, Long> subtaskStartedAt = new HashMap<>();
//            HashMap<Integer, Long> subtaskEndedAt = new HashMap<>();
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
//                        subtaskEndedAt.put(integer, entry.getKey());
                        subtaskEndedAt[integer] = entry.getKey();
                    });
                    reversedRelations.entrySet().forEach(integerHashSetEntry -> entry.getValue().forEach(integer -> integerHashSetEntry.getValue().remove(integer)));
                }
                long finalTime = time;
                reversedRelations.forEach((integer, integers) -> {
                    if(integers.isEmpty() && !subtaskWithEvent[integer]){
                        subtaskWithEvent[integer] = true;
//                        subtaskStartedAt.put(integer, finalTime);
                        long eventTime = finalTime + substasksCosts[integer];
                        events.putIfAbsent(eventTime, new LinkedList<>());
                        events.get(eventTime).add(integer);
                        subtaskStartedAt[integer] = finalTime;
                    }
                });
            }while(!events.isEmpty());
            System.out.println("start and end foreach task : ");
            for (int i = 0; i < subtaskStartedAt.length; i++) {
                System.out.println("i : " + i);
                System.out.println("from : " + subtaskStartedAt[i] + " to : " + subtaskEndedAt[i]);
            }
            completeSegments(subtaskStartedAt, subtaskEndedAt);
        }

        private void completeSegments(long[] subtaskStartedAt, long[] subtaskEndedAt){
            segments = new Segment[(int) Arrays.stream(subtaskEndedAt).distinct().count()];
            List<Long> list = new LinkedList<>();
            list.add(0L);
            System.out.println("stream array");
            Arrays.stream(subtaskEndedAt).distinct().sorted().forEach(value -> {
                System.out.println(value);
                list.add(value);
                list.add(value);
            });
            System.out.println("end stream array");
            System.out.println("list : " + list);
            for (int i = 0; i < segments.length; i++) {
                long from = list.remove(0);
                long to = list.remove(0);
                segments[i] = new Segment(from, to);
            }
            System.out.println("From and to of seg");
            for (int i = 0; i < segments.length; i++) {
                System.out.println("Seg " + i);
                System.out.println("from : " + segments[i].from + " to : " + segments[i].to);
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
            System.out.println("Li : " + segments[segments.length - 1].to);
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
                System.out.println("unitFactor : " + unitFactor);
                long previousDeadline = 0;
                for (Segment segment : mts.segments) {
                    System.out.println("new seg");
                    System.out.println("previousDeadline : " + previousDeadline);
                    double segmentFactor = unitFactor * ((double) segment.substasksContained.size() - 1);
                    System.out.println("segmentFactor : " + segmentFactor);
                    int nbOtherThreads = segment.substasksContained.size() - (int) Math.floor(segmentFactor) - 1;
                    System.out.println("qi : " + nbOtherThreads);
                    if(nbOtherThreads == 0){
                        masterThread.addCost(segment.to - segment.from);
                        System.out.println("cost : " + (segment.to - segment.from));
                    }
                    else {
                        int k;
                        for (k = 0; k < nbOtherThreads; k++) {
                            System.out.println("k : " + k + " cost : " + (segment.to - segment.from));
                            masterThread.addCost(segment.to - segment.from);
                        }
//                    masterThread.addCost();
                        long wcet;
                        long offset;
                        long deadline;
                        long addedJobTotalCost = segment.to - segment.from;
                        System.out.println("addedJobTotalCost : " + addedJobTotalCost);
                        masterThread.addCost((long) ((segmentFactor - Math.floor(segmentFactor)) * addedJobTotalCost));
                        wcet = (long) ((1 + Math.floor(segmentFactor) - segmentFactor) * addedJobTotalCost);
                        offset = getOffset() + previousDeadline;
                        deadline = (long) ((1 + Math.floor(segmentFactor)) * addedJobTotalCost) + offset;
                        System.out.println("Last add to master : " + wcet);
                        System.out.println("deadline : " + deadline);
                        System.out.println("offset : " + offset);
                        otherJobs.add(new StretchedJob(offset, deadline, wcet));
                        k++;
                        for (; k < segment.substasksContained.size(); k++) {
                            wcet = segment.to - segment.from;
                            offset = getOffset() + previousDeadline;
                            deadline = (long) ((1 + segmentFactor) * segment.to - segment.from) +offset;
                            otherJobs.add(new StretchedJob(offset, deadline, wcet));
                        }
                        //probably failed.
//                        previousOffset = previousDeadline + previousOffset;
                    }
                    previousDeadline = masterThread.wcet + getOffset();
                }
            }
        }


/*        MTS mts = new MTS(relations);
        System.out.println("Final segments : ");
        for (Segment segment : mts.segments) {
            System.out.println("Segment : " + "from : " + segment.from + " to : " + segment.to + " tasks : " + segment.substasksContained);
        }*/
    }
}
