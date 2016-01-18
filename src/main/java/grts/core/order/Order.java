package grts.core.order;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import grts.core.tests.NonPreemptiveResponseTimeTest;

import java.util.*;

public class Order {
    private final ArrayList<HashMap<AbstractRecurrentTask, Integer>> orders = new ArrayList<>();

    public Order(TaskSet taskSet) throws Exception {
        findOrders(taskSet);
    }

    private void findOrders(TaskSet taskSet) throws Exception {
        orders.add(new HashMap<>());

        NonPreemptiveResponseTimeTest responseTimeTest = new NonPreemptiveResponseTimeTest();
        //Extraire la liste des tâches
        ArrayList<AbstractRecurrentTask> schedulables = new ArrayList<>();
        taskSet.stream().forEach(schedulable -> schedulables.add((AbstractRecurrentTask) schedulable));

        //Liste des plus hauts priorités => liste des tâches qui n'ont pas encore été assignées pour chaque branche de l'arbre
        ArrayList<ArrayList<AbstractRecurrentTask>> higherTasks = new ArrayList<>();
        higherTasks.add(new ArrayList<>());
        higherTasks.get(0).addAll(schedulables);

        //Liste des tâches déjà assignées pour chaque branche de l'arbre
        ArrayList<ArrayList<AbstractRecurrentTask>> lowerTasks = new ArrayList<>();
        lowerTasks.add(new ArrayList<>());

        for(int i = 0; i < schedulables.size(); i++){
            /*
            * @param schedulable The schedulable to test.
            * @param otherSchedulable The schedulable with a lower priority with the biggest cost.
            * @param taskSet The set of tasks with higher priorities than schedulable.
             */
            if(higherTasks.size() != lowerTasks.size() || orders.size() != higherTasks.size()){
                throw new IllegalStateException("higherTasks.size() != lowerTasks.size() != ret.size()");
            }
            int  maxSize = higherTasks.size();
            for(int j = 0; j < maxSize; j++){
                ArrayList<AbstractRecurrentTask> ht = higherTasks.get(j);
                ArrayList<AbstractRecurrentTask> lt = lowerTasks.get(j);
                int htSize = ht.size();
                List<AbstractRecurrentTask> availableTasks = new LinkedList<>();
                for(int k = 0; k < htSize; k++){
                    List<Schedulable> htcp = new ArrayList<>(ht);
                    AbstractRecurrentTask taskToTest = (AbstractRecurrentTask) htcp.remove(k);
                    Optional<AbstractRecurrentTask> optional = lt.stream().max((o1, o2) -> Long.compare(o1.getWcet(), o2.getWcet()));
                    AbstractRecurrentTask lowerTask;
                    if(!optional.isPresent()){
                        lowerTask = null;
                    }
                    else{
                        lowerTask = optional.get();
                    }
                    if(responseTimeTest.isSchedulable(taskToTest, lowerTask, new TaskSet(htcp))){
                        availableTasks.add(taskToTest);
                    }
                    else{
                        System.err.println("One task in less");
                    }
                }
                if(availableTasks.size() == 0){
                    throw new Exception("Can't schedule this taskset");
                }
                AbstractRecurrentTask taskToAdd = availableTasks.remove(0);
                List<Integer> ints = new LinkedList<>();
                for(int k = 0; k < availableTasks.size(); k++){
                    int index = higherTasks.size();
                    ints.add(index);
                    orders.add(new HashMap<>());
                    orders.get(index).putAll(orders.get(j));
                    higherTasks.add(new ArrayList<>());
                    higherTasks.get(index).addAll(new ArrayList<>(higherTasks.get(j)));
                    lowerTasks.add(new ArrayList<>());
                    lowerTasks.get(index).addAll(new ArrayList<>(lowerTasks.get(j)));
                }
                int priority = orders.get(j).size() + 1;
                orders.get(j).put(taskToAdd, priority);
                ht.remove(taskToAdd);
                lt.add(taskToAdd);
                int intsSize = ints.size();
                for(int k = 0; k < intsSize; k++){
                    int index = ints.remove(0);
                    AbstractRecurrentTask task = availableTasks.remove(0);
                    orders.get(index).put(task, priority);
                    higherTasks.get(index).remove(task);
                    lowerTasks.get(index).add(task);
                }
            }
        }
    }

    private static void properPrint(){

    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder("Priorities struct :\n");
        for(int i = 0; i < orders.size(); i++){
            str.append("Priorities order nb : ").append(i).append("\n");
            HashMap<AbstractRecurrentTask, Integer> map = orders.get(i);
            //            map.forEach((abstractRecurrentTask, integer) -> System.out.println(abstractRecurrentTask.getName() + " at priority : " + integer));
            //Sort pour rendre plus visible le résultat.
            map.entrySet().stream()
                    .sorted((o1, o2) ->
                            o1.getKey().getName().compareTo(o2.getKey().getName()))
                    .forEach(abstractRecurrentTaskIntegerEntry ->
                            str.append(abstractRecurrentTaskIntegerEntry.getKey().getName()).append(" at priority : ").append(abstractRecurrentTaskIntegerEntry.getValue()).append("\n"));
//                            System.out.println(abstractRecurrentTaskIntegerEntry.getKey().getName() + " at priority : " + abstractRecurrentTaskIntegerEntry.getValue()));
        }
        return str.toString();
    }
}
