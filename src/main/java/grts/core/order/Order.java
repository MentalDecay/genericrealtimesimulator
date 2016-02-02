package grts.core.order;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import grts.core.tests.NonPreemptiveResponseTime;
import grts.core.tests.NonPreemptiveResponseTimeTest;

import java.util.*;

public class Order {
    private final ArrayList<HashMap<AbstractRecurrentTask, Integer>> orders = new ArrayList<>();

    public Order(TaskSet taskSet) throws Exception {
        findOrders(taskSet);
    }

    private void findOrders(TaskSet taskSet) throws Exception {
        orders.add(new HashMap<>());

        //Extract a copy of the list of tasks.
        ArrayList<AbstractRecurrentTask> schedulables = new ArrayList<>();
        taskSet.stream().forEach(schedulable -> schedulables.add((AbstractRecurrentTask) schedulable));

        //The list of tasks of higher priorities for each order found. (higher priorities means task not scheduled yet by OPA).
        ArrayList<ArrayList<AbstractRecurrentTask>> higherTasks = new ArrayList<>();
        higherTasks.add(new ArrayList<>());
        //Init the list with all the tasks.
        higherTasks.get(0).addAll(schedulables);

        //The list of tasks of lower priorities for each order found. (lower priorities means task already scheduled by OPA).
        ArrayList<ArrayList<AbstractRecurrentTask>> lowerTasks = new ArrayList<>();
        //This list is empty at the beginning.
        lowerTasks.add(new ArrayList<>());


        //This loop is from 0 to the total number of tasks.
        for(int i = 0; i < schedulables.size(); i++){
            if(higherTasks.size() != lowerTasks.size() || orders.size() != higherTasks.size()){
                throw new IllegalStateException("higherTasks.size() != lowerTasks.size() != ret.size()");
            }

            //Get the number of orders
            int  maxSize = higherTasks.size();
            //This loop is from 0 to the number of orders
            for(int j = 0; j < maxSize; j++){
                //Local variable to have a easier access to the higher and lower tasks of the order.
                ArrayList<AbstractRecurrentTask> ht = higherTasks.get(j);
                ArrayList<AbstractRecurrentTask> lt = lowerTasks.get(j);

                //Get the total number of higher task of the order studied.
                int htSize = ht.size();
                List<AbstractRecurrentTask> availableTasks = new LinkedList<>();
                System.out.println("\n\nPriority nb : " + j);
                System.out.println("Higher tasks : ");
                ht.forEach(abstractRecurrentTask -> System.out.println(abstractRecurrentTask.getName()));
                System.out.println("Lower tasks : ");
                lt.forEach(abstractRecurrentTask -> System.out.println(abstractRecurrentTask.getName()));
                //Foreach higher task of the order.
                for(int k = 0; k < htSize; k++){
                    //Copy of the higher tasks (this list will be modified)
                    List<Schedulable> htcp = new ArrayList<>(ht);
                    AbstractRecurrentTask taskToTest = (AbstractRecurrentTask) htcp.remove(k);
                    System.out.println("taskToTest : " + taskToTest.getName());
                    if(!taskIsSchedulable(taskToTest, lt, htcp)){
//                        System.out.println("Can't set the task" + taskToTest.getName() + " at this priority");
                    }
                    else{
                        availableTasks.add(taskToTest);
                    }
                }

                //If there is no task available at this priority.
                if(availableTasks.size() == 0){
                    throw new Exception("Can't schedule this taskset");
                }
                System.out.println("\nAvailables : ");
                availableTasks.forEach(abstractRecurrentTask -> System.out.println(abstractRecurrentTask.getName()));
                //In the list of tasks available at this priority lvl, the first is taken
                AbstractRecurrentTask taskToAdd = availableTasks.remove(0);

                //List of indexes of the order in which the task has to be added.
                List<Integer> ints = new LinkedList<>();

                //Finds the indexes
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

                //Finds the priority level (the orders have all the same priority level at this time)
                int priority = orders.get(j).size() + 1;

                //Adds the task to the order with the right priority.
                orders.get(j).put(taskToAdd, priority);

                //The task is no longer in higher priorities, its priority is fixed.
                ht.remove(taskToAdd);

                //The tasks will have a lower priority according to the next tasks.
                lt.add(taskToAdd);
                int intsSize = ints.size();

                //Same three previous steps to the other tasks available at this priority level.
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

    private  boolean taskIsSchedulable(AbstractRecurrentTask taskToTest, List<AbstractRecurrentTask> lt, List<Schedulable> htcp){
        Optional<AbstractRecurrentTask> optional = lt.stream().max((o1, o2) -> Long.compare(o1.getWcet(), o2.getWcet()));
        AbstractRecurrentTask lowerTask;
        if(!optional.isPresent()){
            lowerTask = null;
        }
        else{
            lowerTask = optional.get();
        }
        NonPreemptiveResponseTime responseTimeTest = new NonPreemptiveResponseTime();
//        NonPreemptiveResponseTimeTest responseTimeTest = new NonPreemptiveResponseTimeTest();
        return responseTimeTest.isSchedulable(taskToTest, lowerTask, new TaskSet(htcp));
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder("Priorities struct :\n");
        for(int i = 0; i < orders.size(); i++){
            str.append("Priorities order nb : ").append(i).append("\n");
            HashMap<AbstractRecurrentTask, Integer> map = orders.get(i);
            //            map.forEach((abstractRecurrentTask, integer) -> System.out.println(abstractRecurrentTask.getName() + " at priority : " + integer));
            //Sort to have a more readable print
            map.entrySet().stream()
                    .sorted((o1, o2) ->
                            //o1.getKey().getName().compareTo(o2.getKey().getName())
                            Integer.compare(o1.getValue(), o2.getValue())
                            )
                    .forEach(abstractRecurrentTaskIntegerEntry ->
                            str.append(abstractRecurrentTaskIntegerEntry.getKey().getName()).append(" at priority : ").append(abstractRecurrentTaskIntegerEntry.getValue()).append("\n"));
//                            System.out.println(abstractRecurrentTaskIntegerEntry.getKey().getName() + " at priority : " + abstractRecurrentTaskIntegerEntry.getValue()));
        }
        return str.toString();
    }

    public int nbOrders(){
        return orders.size();
    }

    // NOT SAFE, NOT A COPY
    public ArrayList<HashMap<AbstractRecurrentTask, Integer>> getOrders(){
        return orders;
    }

    public HashMap<AbstractRecurrentTask, Integer> getOrderNumber(int nb){
        return orders.get(nb);
    }
}
