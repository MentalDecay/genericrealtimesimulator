package GRTS.main;

import GRTS.core.priority.policies.*;
import GRTS.core.schedulable.AbstractRecurrentTask;
import GRTS.core.Scheduler;
import GRTS.core.schedulable.PeriodicTask;
import GRTS.core.taskset.ITaskSet;
import GRTS.core.taskset.TaskSet;

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
        Scheduler scheduler = new Scheduler(ts, policy);
        scheduler.schedule(ts.getHyperPeriod());
    }
}
