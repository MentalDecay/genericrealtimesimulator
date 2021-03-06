package grts.core.priority.policies;


import grts.core.schedulable.Job;
import grts.core.taskset.TaskSet;

import java.util.Comparator;
import java.util.List;

public class LeastLaxityFirst extends AbstractPriorityPolicy implements IPriorityPolicy {

    /**
     * Creates a new least laxity first policy.
     * @param taskSet The tasks set associated with the policy.
     */
    public LeastLaxityFirst(TaskSet taskSet) {
        super("Least Laxity First", taskSet);
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time) {
        if(activeJobs.isEmpty()){
            return null;
        }
        return activeJobs.stream().min(Comparator.comparing(job -> job.getLaxity(time))).get();
    }
}
