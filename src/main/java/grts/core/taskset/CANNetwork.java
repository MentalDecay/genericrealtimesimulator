package grts.core.taskset;

import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class CANNetwork {
//    XXX unsafe object. Its construction is not completely done after the new
    private final HashMap<Schedulable, CANNetwork> externalSchedulables = new HashMap<>();
    private final List<Schedulable> internalSchedulables;

    public CANNetwork(List<Schedulable> internalSchedulables) {
        this.internalSchedulables = internalSchedulables;
    }

    public void addExternalSchedulable(Schedulable schedulable, CANNetwork source){
        if(externalSchedulables.containsKey(schedulable)){
            throw new IllegalArgumentException("The external schedulable is already in the CANNetwork");
        }
        externalSchedulables.put(schedulable, source);
    }

    public double totalUtilization(){
        ToDoubleFunction<? super Schedulable> fun = schedulable -> {
            PeriodicTask task = (PeriodicTask) schedulable;
            return (double) task.getWcet() / (double) task.getPeriod();
        };
        return internalSchedulables.stream().mapToDouble(fun).sum() + externalSchedulables.keySet().stream().mapToDouble(fun).sum();
    }

    public List<Schedulable> getInternalSchedulables() {
        return Collections.unmodifiableList(internalSchedulables);
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
}
