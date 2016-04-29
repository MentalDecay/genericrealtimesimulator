package grts.core.priority.policies;

import grts.core.exceptions.UnschedulableException;
import grts.core.schedulable.Job;
import grts.core.schedulable.PeriodicTaskEnergyAware;
import grts.core.taskset.TaskSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/04/2016.
 */
public class PFPAsap extends AbstractPriorityPolicy implements IPriorityPolicy {
    // energy replenishment
    private final float pr;

    // energy available in the system
    private float energy = 0;

    private final float eMin;

    private final ArrayList<PeriodicTaskEnergyAware> energyTaskSet = new ArrayList<>();

    PFPAsap(TaskSet taskSet, float pr, float eMin) throws UnschedulableException {
        super("PFPAsap", taskSet);
        if(pr <= 0) {
            throw new IllegalArgumentException("pr <= 0");
        }
        this.pr = pr;

        if(eMin < 0) {
            throw new IllegalArgumentException("emin < 0");
        }
        this.eMin = eMin;

        if(!init(taskSet)) {
            throw new UnschedulableException();
        }
    }

    private boolean init(TaskSet taskSet) {
        taskSet.stream().forEach(schedulable -> {
            if(!(schedulable instanceof PeriodicTaskEnergyAware)) {
                throw new IllegalArgumentException("Can't use PFPasap with non energy aware task.");
            } else {
                energyTaskSet.add((PeriodicTaskEnergyAware)schedulable);
            }
        });
    }

    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time) {
        if(activeJobs.isEmpty()) {
            return null;
        }

        Job firstJob = activeJobs.get(0); // FIXME get highest priority active job
        PeriodicTaskEnergyAware task = (PeriodicTaskEnergyAware) firstJob.getTask();
        if(energyTaskSet.contains(task)) {
            if((energy + pr - eMin) >= (task.getWcec() / task.getWcet())) {
                return firstJob;
            }
        } else {
            return null;
        }
    }
}
