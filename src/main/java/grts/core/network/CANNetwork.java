package grts.core.network;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class CANNetwork {
//    XXX unsafe object. Its construction is not completely done after the new

    private class MyHashMap<K,V> extends HashMap<K,V>{
        @Override
        public int hashCode() {
            int h = 0;
            for (Entry<K, V> kvEntry : entrySet()) {
                h += kvEntry.getKey().hashCode();
            }
            return h;
        }

    }
    private final HashMap<AbstractRecurrentTask, CANNetwork> externalSchedulables = new MyHashMap<>();
    private final List<AbstractRecurrentTask> internalSchedulables;
    private Map<AbstractRecurrentTask, Integer> priorities = new HashMap<>();

    public CANNetwork(List<AbstractRecurrentTask> internalSchedulables) {
        this.internalSchedulables = Objects.requireNonNull(internalSchedulables);
    }

    public void addExternalSchedulable(AbstractRecurrentTask schedulable, CANNetwork source){
        if(externalSchedulables.containsKey(schedulable)){
            throw new IllegalArgumentException("The external schedulable is already in the CANNetwork");
        }
        externalSchedulables.put(schedulable, source);
    }

    public double totalUtilization(){
        ToDoubleFunction<? super AbstractRecurrentTask> fun = schedulable -> (double) schedulable.getWcet() / (double) schedulable.getMinimumInterArrivalTime();
        return internalSchedulables.stream().mapToDouble(fun).sum() + externalSchedulables.keySet().stream().mapToDouble(fun).sum();
    }

    public List<AbstractRecurrentTask> getInternalSchedulables() {
        return Collections.unmodifiableList(internalSchedulables);
    }

    public Map<AbstractRecurrentTask, CANNetwork> getExternalSchedulables() {
        return Collections.unmodifiableMap(externalSchedulables);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("CANNetwork :\nInternal :\n");
        internalSchedulables.stream().forEach(schedulable -> str.append(schedulable).append("\n"));
        if(externalSchedulables.size() > 0) {
            str.append("\nExternal :\n");
            externalSchedulables.keySet().stream().forEach(schedulable -> str.append(schedulable).append("\n"));
        }
        return str.toString();
    }

    public TaskSet getTaskSetFromSchedulables(){
        LinkedList<Schedulable> list = new LinkedList<>();
        list.addAll(internalSchedulables);
        externalSchedulables.keySet().forEach(list::add);
        return new TaskSet(list);
    }

    public void addPriorities(Map<AbstractRecurrentTask, Integer> priorities){
        if(priorities.size() != externalSchedulables.size() + internalSchedulables.size()){
            throw new IllegalArgumentException("The size of priorities should correspond to the number of tasks in this CAN Network");
        }
        this.priorities = priorities;
    }

    public Map<AbstractRecurrentTask, Integer> getPriorities() {
        return Collections.unmodifiableMap(priorities);
    }

    public Integer getTaskPriority(AbstractRecurrentTask task){
        if(!priorities.containsKey(task)){
            throw new IllegalArgumentException("This task isn't in this network or has no priority");
        }
        return priorities.get(task);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CANNetwork)) {
            return false;
        }
        CANNetwork canNetwork = (CANNetwork) obj;
        return this == canNetwork ||
                externalSchedulables.equals(canNetwork.externalSchedulables) &&
                        internalSchedulables.equals(canNetwork.internalSchedulables) &&
                        priorities.equals(canNetwork.priorities);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + internalSchedulables.hashCode();
        hash = hash * 31 + externalSchedulables.hashCode();
        hash = hash * 31 + priorities.hashCode();
        return hash;
    }
}
