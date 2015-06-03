package GRTS.core.priority.policies;


import GRTS.core.schedulable.Job;
import GRTS.core.taskset.ITaskSet;

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
        Job jobToExecute = activeJobs.stream().min(Comparator.comparing(job -> job.getLaxity(time))).get();
        return jobToExecute;
    }
}
