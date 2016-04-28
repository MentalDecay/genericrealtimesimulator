package grts.core.network;

import grts.core.schedulable.AbstractRecurrentTask;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriorityMinimizer {

    /**
     * Used to store the two priorities of a message
     */
    public class Priorities {
        private final int internalPriority;
        private final Optional<Integer> externalPriority;

        public Priorities(int internalPriority, int externalPriority) {
            this.internalPriority = internalPriority;
            this.externalPriority = Optional.of(externalPriority);
        }

        public Priorities(int internalPriority){
            this.internalPriority = internalPriority;
            this.externalPriority = Optional.empty();
        }
    }

    /**
     * Represents a message and the CANNetwork from which it comes.
     */
    private class TaskNetwork implements Comparable<TaskNetwork> {
        private final AbstractRecurrentTask task;
        private final CANNetwork network;

        private TaskNetwork(AbstractRecurrentTask task, CANNetwork network) {
            this.task = task;
            this.network = network;
        }

        @Override
        public int compareTo(TaskNetwork o) {
            int cmp = Integer.compare(network.getTaskPriority(task), o.network.getTaskPriority(o.task));
            if(cmp == 0){
                return -1;
            }
            return cmp;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TaskNetwork)) {
                return false;
            }
            TaskNetwork taskNetwork = (TaskNetwork) obj;
            return this == taskNetwork || task.equals(taskNetwork.task) && network.equals(taskNetwork.network);
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 31 + task.hashCode();
            hash = hash * 31 + network.hashCode();
            return hash;
        }

        @Override
        public String toString() {
            return "task : " + task.getName();
        }
    }

    private class TasksNetworks implements Comparable<TasksNetworks> {
        private final TaskNetwork lowerPriority;
        private final TaskNetwork biggerPriority;

        public TasksNetworks(TaskNetwork taskNetwork1, TaskNetwork taskNetwork2) {
            if(minimizedNetworkPriorities
                    .get(taskNetwork1.network)
                    .get(taskNetwork1.task) <=
                    minimizedNetworkPriorities
                            .get(taskNetwork2.network)
                            .get(taskNetwork2.task)){
                this.lowerPriority = taskNetwork1;
                this.biggerPriority = taskNetwork2;
            }
            else{
                this.lowerPriority = taskNetwork2;
                this.biggerPriority = taskNetwork1;
            }
        }


        @Override
        public int compareTo(TasksNetworks o) {
            //            int cmp = Integer.compare(lowerPriority.network.getTaskPriority(lowerPriority.task), o.lowerPriority.network.getTaskPriority(o.lowerPriority.task));
            int cmp = Integer.compare(minimizedNetworkPriorities.get(lowerPriority.network).get(lowerPriority.task),
                    minimizedNetworkPriorities.get(o.lowerPriority.network).get(o.lowerPriority.task));
            //            System.out.println("this : " + this.toString());
            //            System.out.println("compare to : " + o.toString());
            //            System.out.println("cmp : " + cmp);
            if(cmp != 0){
                //                System.out.println("gg pour la tâche : " + lowerPriority);
                return cmp;
            }
            //            if(lowerPriority.network.equals(o.biggerPriority.network)){
            //                //                System.out.println("if1 : lower : " + lowerPriority);
            //                //                System.out.println("if1 : bigger : " + biggerPriority);
            //                return 1;
            //            }
            //            if(biggerPriority.network.equals(o.lowerPriority.network)){
            //                //                System.out.println("if2 : lower : " + lowerPriority);
            //                //                System.out.println("if2 : bigger : " + biggerPriority);
            //                return -1;
            //            }
            //            return 1;
            //            if(!graph.getAllEdges(lowerPriority, o.lowerPriority).isEmpty()){
            //                System.out.println("this to compareTo ok");
            //                System.out.println("this : " + this);
            //                System.out.println("compareTo : " + o);
            //                return -1;
            //            }
            //            System.out.println("this to compareTo NOT ok");
            //            System.out.println("this : " + this);
            //            System.out.println("compareTo : " + o);
            //            return 1;

            //Useless complexity but avoid to implements a Dijkstra algorithm to extract the information.
            DijkstraShortestPath<TaskNetwork, DefaultEdge> path = new DijkstraShortestPath<>(graph, lowerPriority, o.lowerPriority);
            if(path.getPathEdgeList() != null){
                System.out.println("path exist between " + lowerPriority.task.getName() + " and " + o.lowerPriority.task.getName());
                return -1;
            }
            System.out.println("no path between " + lowerPriority.task.getName() + " and " + o.lowerPriority.task.getName());
            return 1;

        }

        @Override
        public String toString() {
            return "lower : " + lowerPriority + " = " + minimizedNetworkPriorities.get(lowerPriority.network).get(lowerPriority.task) +
                    ", bigger : " + biggerPriority + " = " + minimizedNetworkPriorities.get(biggerPriority.network).get(biggerPriority.task);
        }
    }

    private final Map<CANNetwork, Map<AbstractRecurrentTask, Integer>> oldNetworkPriorities = new HashMap<>();
    private final Map<CANNetwork, Map<AbstractRecurrentTask, Integer>> minimizedNetworkPriorities = new HashMap<>();
    private final DefaultDirectedGraph<TaskNetwork, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private boolean minimizedComputed = false;

    public PriorityMinimizer(GlobalNetwork globalNetwork){
        init(globalNetwork);
        minimizedNetworkPriorities.putAll(oldNetworkPriorities);
    }

    private void init(GlobalNetwork globalNetwork){
        globalNetwork.getNetworks().forEach(canNetwork -> {
            if (canNetwork.getPriorities().size() == 0) {
                throw new IllegalArgumentException("Network without priorities");
            }
            //            oldNetworkPriorities.put(canNetwork, canNetwork.getPriorities());
            //            Stream<Map.Entry<AbstractRecurrentTask, Integer>> stream = canNetwork.getPriorities().entrySet().stream()
            //                    .sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()));
            //            long count = stream.count();
            //            oldNetworkPriorities.put(canNetwork, new HashMap<>());
            //            for (long i = 1; i <= count; i++) {
            //                Map.Entry<AbstractRecurrentTask, Integer> entry = stream.findFirst().get();
            //                oldNetworkPriorities.get(canNetwork).put(entry.getKey(), (int) i);
            //            }
            oldNetworkPriorities.put(canNetwork, new HashMap<>());
            List<Map.Entry<AbstractRecurrentTask, Integer>> sortedList = canNetwork.getPriorities().entrySet().stream()
                    .sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
                    .collect(Collectors.toList());
            int size = sortedList.size();
            for (int i = 1; i <= size; i++) {
                Map.Entry<AbstractRecurrentTask, Integer> entry = sortedList.remove(0);
                oldNetworkPriorities.get(canNetwork).put(entry.getKey(), i);
            }
        });
        System.out.println("old priorities : " + oldNetworkPriorities);
    }

    public Map<CANNetwork, Map<AbstractRecurrentTask, Integer>> getOldNetworkPriorities() {
        return Collections.unmodifiableMap(oldNetworkPriorities);
    }

    public Priorities getTaskOldPriority(AbstractRecurrentTask task){
        return getTaskPriority(task, oldNetworkPriorities);
    }

    private Priorities getTaskPriority(AbstractRecurrentTask task, Map<CANNetwork, Map<AbstractRecurrentTask, Integer>> map){
        Stream<Map.Entry<CANNetwork, Map<AbstractRecurrentTask, Integer>>> stream = map.entrySet().stream()
                .filter(canNetworkMapEntry -> canNetworkMapEntry.getValue().get(task) != null);
        long count = stream.count();
        if(count == 0){
            return null;
        }
        else if(count == 1){
            return new Priorities(stream.findFirst().get().getValue().get(task));
        }
        else if(count == 2){
            Map.Entry<CANNetwork, Map<AbstractRecurrentTask, Integer>> entry = stream.findFirst().get();
            int internalPriority, externalPriority;
            if(entry.getKey().getExternalSchedulables().get(task) == null){
                internalPriority = entry.getValue().get(task);
                externalPriority = stream.findFirst().get().getValue().get(task);
            }
            else{
                externalPriority = entry.getValue().get(task);
                internalPriority = stream.findFirst().get().getValue().get(task);
            }
            return new Priorities(internalPriority, externalPriority);
        }
        else{
            return null;
        }
    }

    public Priorities getTaskMinimizedPriority(AbstractRecurrentTask task){
        if(!minimizedComputed){
            computeMinimizedPriorities();
        }
        return getTaskPriority(task, minimizedNetworkPriorities);
    }

    public Map<CANNetwork, Map<AbstractRecurrentTask, Integer>> getMinimizedNetworkPriorities() {
        if(!minimizedComputed){
            computeMinimizedPriorities();
        }
        return minimizedNetworkPriorities;
    }

    private void computeMinimizedPriorities(){
        HashSet<AbstractRecurrentTask> distantTasks = new HashSet<>();
        //        TreeSet<TasksNetworks> sortedSet = new TreeSet<>();
        HashSet<TasksNetworks> tasksNetworksHashSet = new HashSet<>();
        HashMap<CANNetwork, TreeSet<TaskNetwork>> canNetworkTaskNetworkMap = new HashMap<>();
        createNodesAndExternalEdges(tasksNetworksHashSet, canNetworkTaskNetworkMap, distantTasks);
        createInternalEdges(canNetworkTaskNetworkMap);
        System.out.println("graph : " + graph);
        SzwarcfiterLauerSimpleCycles<TaskNetwork, DefaultEdge> simpleCycles = new SzwarcfiterLauerSimpleCycles<>(graph);
        ArrayList<List<TaskNetwork>> cycleList = new ArrayList<>(simpleCycles.findSimpleCycles().stream()
                .filter(strings -> strings.size() > 2)
                .collect(Collectors.toList()));
        //        System.out.println("cycleList : " + cycleList);
        System.out.println("cycleList");
        cycleList.forEach(System.out::println);
        HashMap<AbstractRecurrentTask, HashSet<Integer>> taskCycleMap = getEdgesIndexCycleMap(cycleList, distantTasks);
        System.out.println("taskCycleMap : " + taskCycleMap);
        HashSet<AbstractRecurrentTask> tasksToDelete = findTasksToDelete(taskCycleMap);
        System.out.println("tasksToDelete : ");
        tasksToDelete.forEach(abstractRecurrentTask -> System.out.println(abstractRecurrentTask.getName()));


        TreeSet<TasksNetworks> sortedSet = createSortedSet(tasksNetworksHashSet, tasksToDelete);
        //        deleteFromSortedSet(graph, sortedSet, tasksToDelete);
        System.out.println("sorted set : ");
        sortedSet.forEach(System.out::println);

        applyRulesFromSortedSet(sortedSet);
        minimizedComputed = true;
    }

    private void createNodesAndExternalEdges(HashSet<TasksNetworks> tasksNetworksHashSet,
                                             HashMap<CANNetwork, TreeSet<TaskNetwork>> canNetworkTaskNetworkMap,
                                             HashSet<AbstractRecurrentTask> distantTasks){
        oldNetworkPriorities.keySet().forEach(canNetwork -> canNetwork.getExternalSchedulables().forEach((abstractRecurrentTask, canNetwork1) -> {
            TaskNetwork externalTaskNetwork = new TaskNetwork(abstractRecurrentTask, canNetwork);
            TaskNetwork internalTaskNetwork = new TaskNetwork(abstractRecurrentTask, canNetwork1);
            canNetworkTaskNetworkMap.computeIfAbsent(canNetwork, canNetwork2 -> new TreeSet<>());
            canNetworkTaskNetworkMap.get(canNetwork).add(externalTaskNetwork);
            canNetworkTaskNetworkMap.computeIfAbsent(canNetwork1, canNetwork2 -> new TreeSet<>());
            canNetworkTaskNetworkMap.get(canNetwork1).add(internalTaskNetwork);
            graph.addVertex(externalTaskNetwork);
            graph.addVertex(internalTaskNetwork);
            distantTasks.add(abstractRecurrentTask);
            graph.addEdge(internalTaskNetwork, externalTaskNetwork);
            graph.addEdge(externalTaskNetwork, internalTaskNetwork);
            //            distantEdges.add(graph.addEdge(internalTaskNetwork, externalTaskNetwork));
            //            distantEdges.add(graph.addEdge(externalTaskNetwork, internalTaskNetwork));
            tasksNetworksHashSet.add(new TasksNetworks(internalTaskNetwork, externalTaskNetwork));
            //            TasksNetworks tasksNetworks = new TasksNetworks(internalTaskNetwork, externalTaskNetwork);
            //            System.out.println("tasksnetworktoadd : " + tasksNetworks);
            //            sortedSet.add(tasksNetworks);
            //            System.out.println("sorted set : ");
            //            sortedSet.forEach(System.out::println);
        }));
    }

    private void createInternalEdges(HashMap<CANNetwork, TreeSet<TaskNetwork>> canNetworkTaskNetworkMap){
        canNetworkTaskNetworkMap.forEach((canNetwork, taskNetworks) -> {
            while (taskNetworks.size() > 1) {
                TaskNetwork taskNetwork = taskNetworks.pollFirst();
                graph.addEdge(taskNetwork, taskNetworks.first());
            }
        });
    }

    private HashMap<AbstractRecurrentTask, HashSet<Integer>> getEdgesIndexCycleMap(ArrayList<List<TaskNetwork>> edges,
                                                                                   HashSet<AbstractRecurrentTask> distantTasks){
        System.out.println("edges : " + edges);
        System.out.println("distant tasks : ");
        distantTasks.forEach(abstractRecurrentTask -> System.out.println(abstractRecurrentTask.getName()));
        HashMap < AbstractRecurrentTask, HashSet < Integer >> edgeCycleMap = new HashMap<>();
        for(int i = 0; i < edges.size(); i++){
            TaskNetwork[] taskNetworks = edges.get(i).toArray(new TaskNetwork[edges.get(i).size()]);
            for(int j = 0; j < taskNetworks.length; j++){
                int next = j+1;
                if(next == taskNetworks.length){
                    next = 0;
                }
                if(!taskNetworks[j].task.equals(taskNetworks[next].task) || !distantTasks.contains(taskNetworks[j].task)){
                    continue;
                }
                //                DefaultEdge edge = graph.getEdge(taskNetworks[j], taskNetworks[next]);
                //                if(edge == null || !distantTasks.contains(taskNetworks[j].task)){
                //                    continue;
                //                }
                graph.removeEdge(graph.getEdge(taskNetworks[j], taskNetworks[next]));
                edgeCycleMap.computeIfAbsent(taskNetworks[j].task, defaultEdge -> new HashSet<>());
                edgeCycleMap.get(taskNetworks[j].task).add(i);
            }
        }
        return edgeCycleMap;
    }

    private HashSet<AbstractRecurrentTask> findTasksToDelete(HashMap<AbstractRecurrentTask, HashSet<Integer>> edgeCycleMap){
        HashSet<AbstractRecurrentTask> tasksToDelete = new HashSet<>();
        int size = edgeCycleMap.size();
        for(int i = 0; i < size; i++){
            Optional<Map.Entry<AbstractRecurrentTask, HashSet<Integer>>> optional =
                    edgeCycleMap.entrySet()
                            .stream()
                            .max((o1, o2) -> Integer.compare(o1.getValue().size(), o2.getValue().size()));
            if(!optional.isPresent()){
                throw new IllegalStateException("No optional, impossible to continue");
            }
            AbstractRecurrentTask task = optional.get().getKey();
            HashSet<Integer> cycles = optional.get().getValue();
            if(cycles.isEmpty()){
                break;
            }
            edgeCycleMap.remove(task);
            tasksToDelete.add(task);
            edgeCycleMap.forEach((defaultEdge, integers) -> cycles.forEach(integers::remove));
        }
        return tasksToDelete;
    }

    private void deleteFromSortedSet(TreeSet<TasksNetworks> sortedSet, List<DefaultEdge> edgesToDelete){
        edgesToDelete.forEach(graph::removeEdge);
        HashSet<AbstractRecurrentTask> tasksToDelete = new HashSet<>();
        edgesToDelete.forEach(defaultEdge -> tasksToDelete.add(graph.getEdgeSource(defaultEdge).task));
        System.out.println("taskToDelete : ");
        tasksToDelete.forEach(System.out::println);
        System.out.println("sorted set : ");
        sortedSet.forEach(System.out::println);
        System.out.println("delete : " + sortedSet
                .removeIf(tasksNetworks -> tasksToDelete.contains(tasksNetworks.lowerPriority.task) || tasksToDelete.contains(tasksNetworks.biggerPriority.task)));
    }

    private TreeSet<TasksNetworks> createSortedSet(HashSet<TasksNetworks> tasksNetworksHashSet, HashSet<AbstractRecurrentTask> tasksToDelete){
        TreeSet<TasksNetworks> sortedSet = new TreeSet<>();
        //        HashSet<AbstractRecurrentTask> tasksToDelete = new HashSet<>();
        //        tasksToDelete.forEach(defaultEdge -> tasksToDelete.add(graph.getEdgeSource(defaultEdge).task));
        //        System.out.println("taskToDelete : ");
        //        tasksToDelete.forEach(System.out::println);
        tasksNetworksHashSet.stream().filter(tasksNetworks -> !tasksToDelete.contains(tasksNetworks.lowerPriority.task)).forEach(sortedSet::add);
        return sortedSet;
    }

    private void applyRulesFromSortedSet(TreeSet<TasksNetworks> sortedSet){
        int size = sortedSet.size();
        for(int i = 0; i < size; i++){
            System.out.println(minimizedNetworkPriorities);
            TasksNetworks firstRule = sortedSet.pollFirst();
            System.out.println("rule : " + firstRule);
            int currentPriority = minimizedNetworkPriorities.get(firstRule.lowerPriority.network).get(firstRule.lowerPriority.task);
            int increase = minimizedNetworkPriorities.get(firstRule.biggerPriority.network).get(firstRule.biggerPriority.task) -
                    currentPriority;
            minimizedNetworkPriorities.get(firstRule.lowerPriority.network).entrySet().stream()
                    .filter(entry -> minimizedNetworkPriorities.get(firstRule.lowerPriority.network).get(entry.getKey()) >= currentPriority)
                    .forEach(entry -> minimizedNetworkPriorities.get(firstRule.lowerPriority.network).replace(entry.getKey(), entry.getValue() + increase));
            actualizeSortedSet(sortedSet, firstRule.lowerPriority.network);
            System.out.println(minimizedNetworkPriorities);
        }
    }

    private void actualizeSortedSet(TreeSet<TasksNetworks> sortedSet, CANNetwork network){
        List<TasksNetworks> toAddList = new LinkedList<>();
        Iterator<TasksNetworks> iterator = sortedSet.iterator();
        while(iterator.hasNext()){
            TasksNetworks tasksNetworks = iterator.next();
            if(tasksNetworks.biggerPriority.network.equals(network) || tasksNetworks.lowerPriority.network.equals(network)){
                toAddList.add(new TasksNetworks(tasksNetworks.lowerPriority, tasksNetworks.biggerPriority));
                iterator.remove();
            }
        }
        sortedSet.addAll(toAddList);
        System.out.println("new sorted set : ");
        sortedSet.forEach(System.out::println);
    }

}
