package GRTS.core.priority.policies;

import GRTS.core.schedulable.Job;
import GRTS.core.taskset.ITaskSet;

import java.util.Comparator;
import java.util.List;

public class DeadlineMonotonic extends AbstractPriorityPolicy implements IPriorityPolicy  {
    public DeadlineMonotonic(ITaskSet taskSet) {
        super("Deadline Monotonic", taskSet);
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time) {
        if(activeJobs.isEmpty()){
            return null;
        }
        Job jobToExecute = activeJobs.stream().min(Comparator.comparing(Job::getDeadlineTask)).get();
        return jobToExecute;
    }
}
