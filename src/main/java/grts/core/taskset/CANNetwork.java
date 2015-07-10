package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class CANNetwork {
//    XXX unsafe object. Its construction is not completely done after the new
    private final HashMap<AbstractRecurrentTask, CANNetwork> externalSchedulables = new HashMap<>();
    private final List<AbstractRecurrentTask> internalSchedulables;
    private final Map<AbstractRecurrentTask, Integer> priorities = new HashMap<>();

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
        this.priorities.putAll(priorities);
    }

    public Map<AbstractRecurrentTask, Integer> getPriorities() {
        return Collections.unmodifiableMap(priorities);
    }
}
