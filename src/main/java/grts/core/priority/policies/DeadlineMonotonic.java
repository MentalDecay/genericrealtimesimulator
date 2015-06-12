package grts.core.priority.policies;

import grts.core.schedulable.Job;
import grts.core.taskset.TaskSet;

import java.util.Comparator;
import java.util.List;

public class DeadlineMonotonic extends AbstractPriorityPolicy implements IPriorityPolicy  {
    public DeadlineMonotonic(TaskSet taskSet) {
        super("Deadline Monotonic", taskSet);
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time) {
        if(activeJobs.isEmpty()){
            return null;
        }
        return activeJobs.stream().min(Comparator.comparing(Job::getDeadlineTask)).get();
    }
}
