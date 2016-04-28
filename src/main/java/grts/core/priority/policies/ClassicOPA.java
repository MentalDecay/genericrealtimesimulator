package grts.core.priority.policies;

import grts.core.exceptions.UnschedulableException;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Job;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import grts.core.tests.NonPreemptiveResponseTimeTest;
import grts.core.tests.NonPreemptiveResponseTimeTestFixed;

import java.util.*;

public class ClassicOPA extends AbstractPriorityPolicy implements IPriorityPolicy {

    private final HashMap<AbstractRecurrentTask, Integer> priorities = new HashMap<>();
    private final NonPreemptiveResponseTimeTestFixed responseTimeTest = new NonPreemptiveResponseTimeTestFixed();


    public ClassicOPA(TaskSet taskSet) throws UnschedulableException {
        super("Classic OPA", taskSet);
        if(!init(taskSet)){
            throw new UnschedulableException();
//            throw new IllegalArgumentException("Can't assign priorities with a classic OPA for this TaskSet");
        }
//        System.out.println(priorities);
    }

    private boolean init(TaskSet taskSet){
        ArrayList<AbstractRecurrentTask> schedulables = new ArrayList<>();
        taskSet.stream().forEach(schedulable1 -> {
            if(!(schedulable1 instanceof AbstractRecurrentTask)){
                throw new IllegalArgumentException("Can't use OPA with non recurrent tasks");
            }
            schedulables.add((AbstractRecurrentTask) schedulable1);
        });
        List<AbstractRecurrentTask> scheduledTasks = new LinkedList<>();
        int size = schedulables.size();
        for(int i = 1; i <= size; i++){
            boolean done = false;
            for(int j = 0; j < schedulables.size(); j++){
                ArrayList<Schedulable> otherTasks = new ArrayList<>(schedulables);
                otherTasks.remove(j);
                Optional<AbstractRecurrentTask> optional = scheduledTasks.stream().max((o1, o2) -> Long.compare(o1.getWcet(), o2.getWcet()));
                AbstractRecurrentTask schedulableLowerPriority = null;
                if(optional.isPresent()){
                    schedulableLowerPriority = optional.get();
                }
                if(responseTimeTest.isSchedulable(schedulables.get(j), schedulableLowerPriority, new TaskSet(otherTasks))){
                    AbstractRecurrentTask schedulable = schedulables.remove(j);
                    scheduledTasks.add(schedulable);
                    priorities.put(schedulable, i);
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
        Optional<Job> job =  activeJobs.stream().max((job1, job2) -> Integer.compare(priorities.get(job1.getTask()), priorities.get(job2.getTask())));
        if(job.isPresent()){
            return job.get();
        }
        return null;
    }

    public Map<AbstractRecurrentTask, Integer> getPriorities() {
        return Collections.unmodifiableMap(priorities);
    }
}
