package grts.main;

import grts.core.priority.policies.*;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.SporadicTask;
import grts.core.simulator.Scheduler;
import grts.core.simulator.SchedulerTimeTriggedLegacy;
import grts.core.schedulable.PeriodicTask;
import grts.core.simulator.Simulator;
import grts.core.taskset.ITaskSet;
import grts.core.taskset.TaskSet;
import grts.logger.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        AbstractRecurrentTask t1 = new PeriodicTask(7, 2, 7, 0, "t1");
        AbstractRecurrentTask t2 = new PeriodicTask(11, 3, 11, 0, "t2");
        AbstractRecurrentTask t3 = new PeriodicTask(13, 5, 13, 0, "t3");
//        AbstractRecurrentTask t1 = new PeriodicTask(4, 1, 4, 0, "t1");
//        AbstractRecurrentTask t2 = new PeriodicTask(5, 2, 5, 0, "t2");
//        AbstractRecurrentTask t3 = new PeriodicTask(10, 3, 10, 0, "t3");
//        AbstractRecurrentTask t1 = new SporadicTask(7, 2, 7, 0, "t1");
//        AbstractRecurrentTask t2 = new SporadicTask(11, 3, 11, 0, "t2");
//        AbstractRecurrentTask t3 = new SporadicTask(13, 5, 13, 0, "t3");


        List<AbstractRecurrentTask> tasks = new LinkedList<>();
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        ITaskSet ts = new TaskSet(tasks);
        IPriorityPolicy policy = new EarliestDeadlineFirst(ts);
//        Logger logger = null;
//        try {
//            logger = new Logger("logs");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Fail logger creation");
//            return;
//        }
//        SchedulerTimeTriggedLegacy scheduler = new SchedulerTimeTriggedLegacy(ts, policy, logger);
//        scheduler.schedule(ts.getHyperPeriod());
//        logger.silentlyClose();

        Simulator simulator = new Simulator(ts, policy);
        long timer = ts.getHyperPeriod();
        simulator.simulate(timer);


    }
}
