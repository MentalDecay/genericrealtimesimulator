package grts.core.simulator;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.events.ActivateJobEvent;
import grts.core.simulator.events.IEvent;
import grts.core.taskset.ITaskSet;

import java.util.List;
import java.util.TreeSet;

public class Simulator {


    private final TreeSet<IEvent> eventTreeSet = new TreeSet<>();
    private final Scheduler scheduler;
    private final ITaskSet taskSet;

    public Simulator(ITaskSet taskSet, IPriorityPolicy policy) {
        this.scheduler = new Scheduler(policy);
        this.taskSet = taskSet;
    }

    public void simulate(long time){
        initEventTreeSet();
        System.out.println("init : ");
        eventTreeSet.forEach(System.out::println);
        System.out.println("\n\n\n");
        while(!scheduler.isOver()){
            IEvent event = eventTreeSet.pollFirst();
            System.out.println("Process event : " + event);
            System.out.println("Other events : ");
            eventTreeSet.forEach(System.out::println);
            if(event.getTime() > time){
                //Simulation is over.
                System.out.println("End of simulation by timer");
                return;
            }
            List<IEvent> events = scheduler.performEvent(event);
            events.forEach(eventTreeSet::add);
            System.out.println("\nCreated events : ");
            events.forEach(System.out::println);
            System.out.println("\n\n\n");
        }
        System.out.println("End of simulation by deadline missed");
    }

    private void initEventTreeSet(){
        taskSet.getRecurrentTasks().forEach(task -> {
            Job firstJob = task.getFirstJob();
            System.out.println(firstJob);
            eventTreeSet.add(new ActivateJobEvent(scheduler, firstJob.getActivationTime(), firstJob));
        });
    }
}
