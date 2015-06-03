package GRTS.core.priority.policies;

import GRTS.core.schedulable.Job;
import GRTS.core.taskset.ITaskSet;

import java.util.Comparator;
import java.util.List;

public class EarliestDeadlineFirst extends AbstractPriorityPolicy implements IPriorityPolicy {

    /**
     * Uses the Earliest Deadline First policy. Priorities are not fixed, they can change during a job according to each deadline of jobs.
     */

    public EarliestDeadlineFirst(ITaskSet taskSet) {
        super("Earliest Deadline First", taskSet);
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time){
        if(activeJobs.isEmpty()){
            return null;
        }
        Job jobToExecute = activeJobs.stream().min(Comparator.comparing(Job::getDeadlineTime)).get();
        return jobToExecute;
    }
}
