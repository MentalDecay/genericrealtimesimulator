package grts.main;

import grts.core.formula.Formula;
import grts.core.generator.UUnifastMonoProc;
import grts.core.order.Order;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


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

        /*for(int j = 1; j <= 100; j++) {
            UUnifastMonoProc generator = new UUnifastMonoProc(5, 0.8);
            TaskSet taskSet;
            Order order;
            long hyperPeriod;
            while (true) {
                taskSet = generator.generateUUnifastMonoProc(50, 0.1);
                try {
                    System.out.println("begin");
                    order = new Order(taskSet);
                    hyperPeriod = HyperPeriod.compute(taskSet);
                    if (order.nbOrders() == 2 && hyperPeriod < 2000) {
                        break;
                    }
                } catch (Exception e) {
                    //Nothing to do, generate another tasks set.
                }
            }

            System.out.println("TaskSet : ");
            taskSet.stream().forEach(schedulable -> System.out.println(schedulable + "\n"));
            System.out.println("Orders : \n" + order);
            AbstractRecurrentTask tasksToCompare[] = getTasksToCompare(order);
            AbstractRecurrentTask taskFromFirstOrderToCompare = tasksToCompare[0];
            AbstractRecurrentTask taskFromSecondOrderToCompare = tasksToCompare[1];
            System.out.println("Tasks to compare : " + taskFromFirstOrderToCompare.getName() + " and " + taskFromSecondOrderToCompare.getName());

            long[] valuesOrder1 = Formula.apply(hyperPeriod, taskFromFirstOrderToCompare, order.getOrderNumber(0));
            long[] valuesOrder2 = Formula.apply(hyperPeriod, taskFromSecondOrderToCompare, order.getOrderNumber(1));

            System.out.println("Values order 1 : ");
            for (int i = 0; i < valuesOrder1.length; i++) {
                System.out.print("f : " + i + "(t) = ");
                System.out.println(valuesOrder1[i]);
            }
//            String firstFile = "workload1.dat";
            String firstFile = "results/workload"+j+".1.dat";
//            String secondFile = "workload2.dat";
            String secondFile = "results/workload"+j+".2.dat";
            String gpFileName = "results/workload"+j+".gp";
            String pdfFileName = "results/workload"+j+".pdf";
            generateGPFile(gpFileName, pdfFileName, 50, firstFile, secondFile);
            generateDatFile(firstFile, valuesOrder1);
            generateDatFile(secondFile, valuesOrder2);
            writeTaskSet("results/taskset"+j+".txt", taskSet);
            try {
                Runtime.getRuntime().exec("gnuplot " + gpFileName);
            } catch (IOException e) {
                System.err.println("Can't apply gnuplot cmd");
                return;
            }
        }*/

        /*PeriodicTask t1 = new PeriodicTask(8, 1, 8, 0, "t1");
        PeriodicTask t2 = new PeriodicTask(11, 3, 11, 0, "t2");
        PeriodicTask t3 = new PeriodicTask(4, 1, 4, 0, "t3");
        PeriodicTask t4 = new PeriodicTask(12, 1, 12, 0, "t4");
        PeriodicTask t5 = new PeriodicTask(8, 1, 8, 0, "t5");
        List<Schedulable> l = new LinkedList<>();
        l.add(t1);
        l.add(t2);
        l.add(t3);
        l.add(t4);
        l.add(t5);
        TaskSet taskSet = new TaskSet(l);
        Order order;

        try {
            order = new Order(taskSet);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        long hyperPeriod = HyperPeriod.compute(taskSet);
        AbstractRecurrentTask tasksToCompare[] = getTasksToCompare(order);
        AbstractRecurrentTask taskFromFirstOrderToCompare = tasksToCompare[0];
        AbstractRecurrentTask taskFromSecondOrderToCompare = tasksToCompare[1];
        long[] valuesOrder1 = Formula.apply(hyperPeriod, taskFromFirstOrderToCompare, order.getOrderNumber(0));
        long[] valuesOrder2 = Formula.apply(hyperPeriod, taskFromSecondOrderToCompare, order.getOrderNumber(1));

        System.out.println("Orders : " + order);

        for(int i = 0; i < hyperPeriod; i++){
            System.out.println("f(" + i + ") = " + valuesOrder1[i]);
        }
        generateGPFile("test.gp", "test.pdf", 50, "test.dat", "hj");
        generateDatFile("test.dat", valuesOrder1);*/


        //        System.out.println("TaskSet : ");
        //        taskSet.stream().forEach(schedulable -> System.out.println(schedulable + "\n"));

        /*PeriodicTask t1 = new PeriodicTask(25, 1, 25, 0, "t1");
        PeriodicTask t2 = new PeriodicTask(16, 1, 16, 0, "t2");
        PeriodicTask t3 = new PeriodicTask(35, 12, 35, 0, "t3");
        PeriodicTask t4 = new PeriodicTask(15, 2, 15, 0, "t4");
        PeriodicTask t5 = new PeriodicTask(23, 4, 23, 0, "t5");
        List<Schedulable> l = new LinkedList<>();
        l.add(t1);
        l.add(t2);
        l.add(t3);
        l.add(t4);
        l.add(t5);
        TaskSet taskSet = new TaskSet(l);
        Order order;
        try {
            order = new Order(taskSet);
            System.out.println("Nb orders : " + order.nbOrders());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println(order);*/
    }

    private static void writeTaskSet(String fileName, TaskSet taskSet){
        List<String> lines = new LinkedList<>();
        taskSet.stream().forEach(schedulable -> lines.add(schedulable.toString()));
        try {
            Files.write(Paths.get(fileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't write the taskset file");
        }
    }

    private static AbstractRecurrentTask[] getTasksToCompare(Order order){
        AbstractRecurrentTask[] ret = new AbstractRecurrentTask[2];
        if(order.nbOrders() != 2){
            throw new IllegalArgumentException("Comparison of two orders without two orders");
        }
        ArrayList<HashMap<AbstractRecurrentTask, Integer>> orders = order.getOrders();
        HashMap<AbstractRecurrentTask, Integer> order1 = orders.get(0);
        HashMap<AbstractRecurrentTask, Integer> order2 = orders.get(1);
        if(order1.size() != order2.size()){
            throw new IllegalArgumentException("Orders not of the same size");
        }
        List<AbstractRecurrentTask> o1 = order1.entrySet().stream()
                .sorted((o11, o2) -> Integer.compare(o11.getValue(), o2.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<AbstractRecurrentTask> o2 = order2.entrySet().stream()
                .sorted((o11, o21) -> Integer.compare(o11.getValue(), o21.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        Iterator<AbstractRecurrentTask> it1 = o1.iterator();
        Iterator<AbstractRecurrentTask> it2 = o2.iterator();
        int nb = 0;
        while(it1.hasNext() && it2.hasNext()){
            nb++;
            AbstractRecurrentTask t1 = it1.next();
            AbstractRecurrentTask t2 = it2.next();
            if(!t1.equals(t2)){
                ret[0] = t1;
                ret[1] = t2;
                System.out.println("Nb priority : " + nb);
                return ret;
            }
        }
        throw new IllegalArgumentException("Can't find the tasks to compare");
    }

    private static void generateGPFile(String gpFileName, String pdfName, long hyperPeriod, String firstFileName, String secondFileName){
        List<String> lines = new LinkedList<>();
        lines.add("#!/usr/bin/env gnuplot\n" +
                "\n" +
                "set terminal pdfcairo\n" +
                "set output '" + pdfName +"'\n" +
//                "set output 'workload.pdf'\n" +
                "\n" +
                "set key left\n" +
                "set xrange [0:"+hyperPeriod+"]\n" +
                "set yrange [0:"+hyperPeriod+"]\n" +
                "set pointsize 0.5\n" +
                "\n" +
                "set linetype 1 linecolor rgb \"dark-green\" linewidth 2\n" +
                "set linetype 2 linecolor rgb \"red\"        linewidth 2 pointtype 7\n" +
                "set linetype 3 linecolor rgb \"blue\" linewidth 2 pointtype 7\n" +
                "\n" +
                "f(x)=x\n" +
                "\n" +
                "plot f(x) linetype 1 title 'y=t', \\\n" +
//                "  'workload.dat' with steps linetype 2 title 'order 1', \\\n" +
                "  '"+firstFileName+"' with steps linetype 2 title 'order 1', \\\n" +
//                "  'workload2.dat' with steps linetype 3 title 'order 2'");
                "  '"+secondFileName+"' with steps linetype 3 title 'order 2'");
        try {
            Files.write(Paths.get(gpFileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't write the .gp file");
        }
    }

    private static void generateDatFile(String datFileName, long[] values){
        List<String> lines = new LinkedList<>();
        for(int i = 0; i < values.length; i++){
            lines.add(i + " " + values[i] + ".0");
        }
        try {
            Files.write(Paths.get(datFileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't write the .dat file");
        }
    }
}
