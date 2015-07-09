package grts.core.priority.policies;

import grts.core.schedulable.Job;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import grts.core.tests.ResponseTimeTest;

import java.util.*;

public class ClassicOPA extends AbstractPriorityPolicy implements IPriorityPolicy {

    private final HashMap<Schedulable, Integer> priorities = new HashMap<>();
    private final ResponseTimeTest responseTimeTest = new ResponseTimeTest();


    public ClassicOPA(TaskSet taskSet) {
        super("Classic OPA", taskSet);
        if(!init(taskSet)){
            throw new IllegalArgumentException("Can't assign priorities with a classic OPA for this TaskSet");
        }
        System.out.println(priorities);
    }

    /*private boolean init(TaskSet taskSet){
        List<Schedulable> schedulables = taskSet.stream().collect(Collectors.toList());
        int count = schedulables.size();
        HashMap<Integer, List<Schedulable>> prioritiesDoneMap = new HashMap<>();
        int currentPriority = 1;
        while(priorities.size() != count){
//            Optional<Schedulable> optional = schedulables.stream().filter(schedulable -> {
//                if (prioritiesDoneMap.containsKey(currentPriority) && prioritiesDoneMap.get(currentPriority).contains(schedulable)) {
//                    return false;
//                }
//                List<Schedulable> otherTasks = schedulables.stream().filter(schedulable1 -> !schedulable1.equals(schedulable)).collect(Collectors.toList());
//                return responseTimeTest.isSchedulable(schedulable, new TaskSet(otherTasks));
//            }).findFirst();
//            if()
        }
    }*/

    private boolean init(TaskSet taskSet){
        ArrayList<Schedulable> schedulables = new ArrayList<>();
        taskSet.stream().forEach(schedulables::add);
        int size = schedulables.size();
        for(int i = 1; i <= size; i++){
            boolean done = false;
            for(int j = 0; j < schedulables.size(); j++){
                ArrayList<Schedulable> otherTasks = new ArrayList<>(schedulables);
                otherTasks.remove(j);
                if(responseTimeTest.isSchedulable(schedulables.get(j), new TaskSet(otherTasks))){
                    priorities.put(schedulables.remove(j), i);
                    done = true;
                    break;
                }
            }
            if(!done){
                return false;
            }
        }
        return true;
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time) {
        return activeJobs.stream().max((job1, job2) -> Integer.compare(priorities.get(job1.getTask()), priorities.get(job2.getTask()))).get();
    }
}
