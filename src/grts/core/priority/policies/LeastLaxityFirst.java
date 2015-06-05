package grts.core.priority.policies;


import grts.core.schedulable.Job;
import grts.core.taskset.ITaskSet;

import java.util.Comparator;
import java.util.List;

public class LeastLaxityFirst extends AbstractPriorityPolicy implements IPriorityPolicy {

    public LeastLaxityFirst(ITaskSet taskSet) {
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
