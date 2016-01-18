package grts.main;

import grts.core.generator.PeriodicTasksGenerator;
import grts.core.generator.UUnifastMonoProc;
import grts.core.order.Order;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;

import java.util.HashSet;


public class Main {

    public static void main(String[] args) {
        //Générer un jeu de tâches sous-chargé.
        /*long hyperPeriod = 2001;
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
        }*/


/*        int valid = 0;
        HashSet<Long> nbHyperPeriods = new HashSet<>();
        for(int i = 0 ; i < 1000; i++) {
            UUnifastMonoProc generator = new UUnifastMonoProc(5, 0.8);
            TaskSet taskSet = generator.generateUUnifastMonoProc(20, 0.1);
            try {
                Order order = new Order(taskSet);
                if(order.nbOrders() == 2){
                    long hyperPeriod = HyperPeriod.compute(taskSet);
                    System.out.println("hyper period : " + hyperPeriod);
                    nbHyperPeriods.add(hyperPeriod);
                    valid++;
                }
                else{
                    i--;
                }
//                System.out.println("nb orders : " + order.nbOrders());
            } catch (Exception e) {
                i--;
//                System.err.println("Can't schedule this taskSet");
            }
        }
        System.out.println("Nb with two orders : " + valid);
        System.out.println("Nb hyper periods : " + nbHyperPeriods.size());*/
        UUnifastMonoProc generator = new UUnifastMonoProc(5, 0.8);
        TaskSet taskSet;
        Order order;
        while(true){
            taskSet = generator.generateUUnifastMonoProc(20, 0.1);
            try{
                order = new Order(taskSet);
                if(order.nbOrders() == 2 && HyperPeriod.compute(taskSet) < 2000){
                    break;
                }
            } catch (Exception e) {
                //Nothing to do, generate another tasks set.
            }
        }

        System.out.println("TaskSet : ");
        taskSet.stream().forEach(schedulable -> System.out.println(schedulable + "\n"));
        System.out.println("Orders : \n" + order);





        //        System.out.println("TaskSet : ");
        //        taskSet.stream().forEach(schedulable -> System.out.println(schedulable + "\n"));


    }
}
