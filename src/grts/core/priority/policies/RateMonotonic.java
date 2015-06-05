package grts.core.priority.policies;

import grts.core.schedulable.Job;
import grts.core.taskset.ITaskSet;

import java.util.Comparator;
import java.util.List;

public class RateMonotonic extends AbstractPriorityPolicy implements IPriorityPolicy {

    /**
     * Uses a rate monotonic priority policy. Uses fifo policy in case of equals periods.
     */


    public RateMonotonic(ITaskSet taskSet) {
        super("Rate Monotonic", taskSet);
    }


    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time){
        // Highest priority scheduling
        if(activeJobs.isEmpty()){
            return null;
        }
        return activeJobs.stream().min(Comparator.comparing(Job::getTaskInterArrivalTime)).get();
    }
}
