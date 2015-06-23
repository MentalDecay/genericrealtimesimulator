package grts.core.simulator.events;


import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.util.AbstractMap;
import java.util.List;

public class ChooseJobEvent extends AbstractEvent implements Event {

    /**
     * Creates a new Choose Job Event. This event is made to allow the scheduler to chose the job(s) to execute.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     */
    public ChooseJobEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

    @Override
    public void handle() {
        /* LEGACY
        if(!getScheduler().getActivatedJobs().isEmpty()){
            Job jobToExecute = getScheduler().getPriorityPolicy().choseJobToExecute(getScheduler().getActivatedJobs(), getTime());

            if(jobToExecute != null) {
                System.out.println("job to execute : " + jobToExecute);
            }

            if(getScheduler().getExecutingJob() == null){
                System.out.println("executing job : " + getScheduler().getExecutingJob());

                getScheduler().addEvent(new JobExecutionStartEvent(getScheduler(), getTime(), jobToExecute));
            }
            else if (getScheduler().getExecutingJob() != jobToExecute){

                getScheduler().addEvent(new PreemptionEvent(getScheduler(), getTime(), jobToExecute));
            }
            else{
                //Nothing to do, the executing job has the highest priority
            }
        }
        */

        List<AbstractMap.SimpleEntry<Job, Integer>> list =  getScheduler().getProcessorPolicy().chooseNextJobs(getTime());
        if(!list.isEmpty()){
            list.forEach(entry -> {
                if(getScheduler().getExecutingJob(entry.getValue()) == null){
                    getScheduler().addEvent(new JobExecutionStartEvent(getScheduler(), getTime(), entry.getKey(), entry.getValue()));
                }
                else if (getScheduler().getExecutingJob(entry.getValue()) != entry.getKey()){
                    getScheduler().addEvent(new PreemptionEvent(getScheduler(), getTime(), entry.getKey(), entry.getValue()));
                }
            });
        }

    }

    @Override
    public String toString() {
        return "ChooseJobEvent : time : " + getTime();
    }

    @Override
    public int getPriority() {
        return 7;
    }

    @Override
    public String getName() {
        return "Choose Job Event";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ChooseJobEvent)){
            return false;
        }
        ChooseJobEvent event = (ChooseJobEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime();
    }

}
