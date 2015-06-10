package grts.core.priority.policies;

import grts.core.schedulable.Job;
import grts.core.taskset.TaskSet;

import java.util.Comparator;
import java.util.List;

public class EarliestDeadlineFirst extends AbstractPriorityPolicy implements IPriorityPolicy {

    /**
     * Uses the Earliest Deadline First policy. Priorities are not fixed, they can change during a job according to each deadline of jobs.
     */

    public EarliestDeadlineFirst(TaskSet taskSet) {
        super("Earliest Deadline First", taskSet);
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time){
        if(activeJobs.isEmpty()){
            return null;
        }
        return activeJobs.stream().min(Comparator.comparing(Job::getDeadlineTime)).get();
    }
}
