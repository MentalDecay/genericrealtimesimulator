package grts.main;

import grts.core.generator.PeriodicTasksGenerator;
import grts.core.order.Order;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;


public class Main {

    public static void main(String[] args) {
        //Générer un jeu de tâches sous-chargé.
        long hyperPeriod = 2001;
        TaskSet taskSet = null;
        while(hyperPeriod > 2000) {
            PeriodicTasksGenerator generator = new PeriodicTasksGenerator(5, 1, 0.6);
            taskSet = generator.generateImplicitPeriodicTaskSet(15, 0.5);
            hyperPeriod = HyperPeriod.compute(taskSet);
        }
        System.out.println("hyperPeriod : " + hyperPeriod + "\n");
        try {
            Order order = new Order(taskSet);
            System.out.println(order);
        } catch (Exception e) {
            System.err.println("Can't schedule this taskSet");
        }
    }
}
