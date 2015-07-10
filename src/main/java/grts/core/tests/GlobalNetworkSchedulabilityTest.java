package grts.core.tests;

import grts.core.exceptions.UnschedulableException;
import grts.core.priority.policies.ClassicOPA;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.CANNetwork;
import grts.core.taskset.GlobalNetwork;
import grts.core.taskset.TaskSet;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GlobalNetworkSchedulabilityTest {

    private final BiFunction<Schedulable, Map.Entry<CANNetwork, CANNetwork>, Map.Entry<Long, Long>> computeDeadlines;

    public GlobalNetworkSchedulabilityTest() {
        computeDeadlines = this::deadlineSplitWithResponseTime;
    }

    private Map.Entry<Long, Long> fairDeadlinesSplit(Schedulable schedulable, Map.Entry<CANNetwork, CANNetwork> networks){
        long deadline = schedulable.getDeadline();
//      -1 is the traveling time
        return new AbstractMap.SimpleEntry<>(deadline / 2 - 1, deadline / 2);
    }

    private Map.Entry<Long, Long> deadlineSplitWithResponseTime(Schedulable schedulable, Map.Entry<CANNetwork, CANNetwork> networks){
        long totalDeadline = schedulable.getDeadline();
        List<Schedulable> higherPriorities = new LinkedList<>();
        Map<AbstractRecurrentTask, Integer> priorities = networks.getKey().getPriorities();
        int taskPriority = priorities.get((AbstractRecurrentTask) schedulable);
        networks.getKey().getTaskSetFromSchedulables().stream()
                .filter(schedulable1 -> priorities.get((AbstractRecurrentTask) schedulable1) > taskPriority)
                .forEach(higherPriorities::add);
        Optional<Schedulable> optional = networks.getKey().getTaskSetFromSchedulables().stream()
                .filter(schedulable1 -> priorities.get((AbstractRecurrentTask) schedulable1) < taskPriority)
                .max((o1, o2) -> Long.compare(o1.getWcet(), o2.getWcet()));
        NonPreemptiveResponseTimeTest responseTimeTest = new NonPreemptiveResponseTimeTest();
        Schedulable lowerTask = null;
        if(optional.isPresent()){
            lowerTask = optional.get();
        }
        long responseTime = responseTimeTest.computeResponseTime(schedulable, lowerTask, new TaskSet(higherPriorities));
//        System.out.println("responseTime : " + responseTime);
        long toDeadline = totalDeadline - (responseTime + 1);
//        System.out.println("from deadline : " + responseTime);
//        System.out.println("to deadline : " + toDeadline);
        return new AbstractMap.SimpleEntry<>(responseTime, toDeadline);
    }

    public boolean isSchedulable(GlobalNetwork globalNetwork){
        List<CANNetwork> networks = globalNetwork.getNetworks();
//      A first application of OPA may be useful if the priorities are needed to compute the deadlines.
        HashMap<CANNetwork, List<AbstractRecurrentTask>> localTasks = new HashMap<>();
        HashMap<AbstractRecurrentTask, Map.Entry<CANNetwork, CANNetwork>> distantTasks = new HashMap<>();
        try {
            initLocalAndDistantMaps(networks, localTasks, distantTasks);
        } catch (UnschedulableException e) {
            return false;
        }
//        System.out.println("localTasks : " + localTasks);
//        HashMap<AbstractRecurrentTask, Map.Entry<AbstractRecurrentTask, AbstractRecurrentTask>> oldSchedulableToNews = new HashMap<>();
        HashMap<CANNetwork, List<Map.Entry<AbstractRecurrentTask, CANNetwork>>> newDistantTasks = createNewTasks(localTasks, distantTasks/*, oldSchedulableToNews*/);
        HashMap<CANNetwork, CANNetwork> oldToNewNetworks = new HashMap<>();
        localTasks.forEach((canNetwork, abstractRecurrentTasks) -> oldToNewNetworks.put(canNetwork, new CANNetwork(abstractRecurrentTasks)));
        newDistantTasks.forEach((canNetwork, entries) -> {
            CANNetwork newNetwork = oldToNewNetworks.get(canNetwork);
            entries
                    .forEach(abstractRecurrentTaskCANNetworkEntry ->
                            newNetwork.addExternalSchedulable(abstractRecurrentTaskCANNetworkEntry.getKey(), abstractRecurrentTaskCANNetworkEntry.getValue()));
        });
        List<CANNetwork> canNetworks = oldToNewNetworks.values().stream().collect(Collectors.toList());
//        System.out.println("networks : ");
//        canNetworks.forEach(System.out::println);
        for (CANNetwork canNetwork : canNetworks) {
            ClassicOPA opa;
            try {
                opa = new ClassicOPA(canNetwork.getTaskSetFromSchedulables());
            } catch (UnschedulableException e) {
                return false;
            }
            canNetwork.addPriorities(opa.getPriorities());
        }
        return true;
    }

    private HashMap<CANNetwork, List<Map.Entry<AbstractRecurrentTask, CANNetwork>>> createNewTasks(HashMap<CANNetwork, List<AbstractRecurrentTask>> localTasks,
                                                 HashMap<AbstractRecurrentTask, Map.Entry<CANNetwork, CANNetwork>> distantTasks){
        HashMap<CANNetwork, List<Map.Entry<AbstractRecurrentTask, CANNetwork>>> newDistantTasks = new HashMap<>();
        distantTasks.forEach((task, canNetworkCANNetworkEntry) -> {
            Map.Entry<Long, Long> deadlines = computeDeadlines.apply(task, canNetworkCANNetworkEntry);
//            System.out.println("deadlines : " + deadlines);
            PeriodicTask fromTask = new PeriodicTask(task.getMinimumInterArrivalTime(), task.getWcet(), deadlines.getKey(), task.getOffset(), task.getName()+"from");
            PeriodicTask toTask = new PeriodicTask(task.getMinimumInterArrivalTime(), task.getWcet(), deadlines.getValue(), task.getOffset(), task.getName()+"to");
            localTasks.get(canNetworkCANNetworkEntry.getKey()).add(fromTask);
            newDistantTasks.computeIfAbsent(canNetworkCANNetworkEntry.getValue(), canNetwork -> new LinkedList<>());
            newDistantTasks.get(canNetworkCANNetworkEntry.getValue()).add(new AbstractMap.SimpleEntry<>(toTask, canNetworkCANNetworkEntry.getKey()));
        });
        return newDistantTasks;

    }

    private void initLocalAndDistantMaps(List<CANNetwork> networks,
                                         HashMap<CANNetwork, List<AbstractRecurrentTask>> localTasks,
                                         HashMap<AbstractRecurrentTask, Map.Entry<CANNetwork, CANNetwork>> distantTasks) throws UnschedulableException {
        for(CANNetwork network : networks){
            ClassicOPA opa = new ClassicOPA(network.getTaskSetFromSchedulables());
            network.addPriorities(opa.getPriorities());
            localTasks.put(network, new LinkedList<>(network.getInternalSchedulables()));
            Map<AbstractRecurrentTask, CANNetwork> networkDistantTasks = network.getExternalSchedulables();
            networkDistantTasks.entrySet().forEach(schedulableCANNetworkEntry -> {
//                System.out.println("try remove : " + schedulableCANNetworkEntry.getKey());
//                System.out.println("from : " + network);
                localTasks.get(schedulableCANNetworkEntry.getValue()).remove(schedulableCANNetworkEntry.getKey());
                distantTasks.put(schedulableCANNetworkEntry.getKey(), new AbstractMap.SimpleEntry<>(schedulableCANNetworkEntry.getValue(), network));
            });
        }
    }
}
