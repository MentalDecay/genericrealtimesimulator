package grts.core.formula;

import grts.core.schedulable.AbstractRecurrentTask;

import java.util.*;
import java.util.stream.Collectors;

public class Formula {

    public Formula() {
    }

    public static long[] apply(long hyperPeriod, AbstractRecurrentTask task, HashMap<AbstractRecurrentTask, Integer> priorities){
        long[] ret = new long[(int)hyperPeriod];
        for(int i = 0; i < hyperPeriod; i++){
            final int j = i;
            Optional<AbstractRecurrentTask> opt = getLowerTasks(task, priorities).stream().max((o1, o2) -> Long.compare(o1.getWcet(), o2.getWcet()));
            long block;
            if(!opt.isPresent()){
                block = 0;
            }
            else{
                block = opt.get().getWcet() - 1;
            }
            ret[i] = ((long)(Math.floor((double) i / (double) task.getMinimumInterArrivalTime()))) * task.getWcet()
            + getHigherPriorities(task, priorities).stream()
                    .mapToLong(value -> (1l + (long) (Math.floor((double) j / (double) value.getMinimumInterArrivalTime()))) * value.getWcet()).
                    sum()
                    + block;
        }
        return ret;
    }

    private static List<AbstractRecurrentTask> getLowerTasks(AbstractRecurrentTask task, HashMap<AbstractRecurrentTask, Integer> priorities){
        int taskPriority = priorities.get(task);
        return priorities
                .entrySet()
                .stream()
                .filter(abstractRecurrentTaskIntegerEntry -> abstractRecurrentTaskIntegerEntry.getValue() < taskPriority)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static List<AbstractRecurrentTask> getHigherPriorities(AbstractRecurrentTask task, HashMap<AbstractRecurrentTask, Integer> priorities){
        int taskPriority = priorities.get(task);
        return priorities
                .entrySet()
                .stream()
                .filter(abstractRecurrentTaskIntegerEntry -> abstractRecurrentTaskIntegerEntry.getValue() > taskPriority)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
