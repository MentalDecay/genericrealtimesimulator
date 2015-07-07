package grts.core.generator;

import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.CANNetwork;
import grts.core.taskset.GlobalNetwork;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalNetworkGenerator {
    private final int n;
    private final int m;
    private final double u;
    private final double localPerCent;
    private final Random random = new Random();

    /**
     * Creates a new GlobalNetworkGenerator.
     * @param n The total number of tasks.
     * @param m The number of networks.
     * @param u The utilization of the GlobalNetwork.
     * @param localPerCent The percentage of local tasks.
     */
    public GlobalNetworkGenerator(int n, int m, double u, double localPerCent) {
        if(n < 1 || m <= 1 || u <= 0 || u > 1 || localPerCent > 1){
            throw new IllegalArgumentException("Can't create a new GlobalNetworkGenerator with this parameters : n :" + n + " m : " + m + " u : " + u + "localPerCent : " + localPerCent);
        }
        this.n = n;
        this.m = m;
        this.u = u;
        this.localPerCent = localPerCent;
    }


    /**
     * Completes the lists of utilizations.
     * @param localUtilizations The list of local utilizations to complete.
     * @param distantUtilizations The list of distant utilizations to complete.
     * @return True if the list are completed correctly, false otherwise
     */
    private boolean generateUtilizations(List<Double> localUtilizations, List<Double> distantUtilizations){
        double mU = m * u;
        double sumU;
        sumU = mU;
        double nextSumU;
        for (int i = 0; i < n - 1; i++) {
            nextSumU = sumU * Math.pow(random.nextDouble(), 1. / ((double) n - (double) i));
            boolean isDistant = random.nextDouble() > localPerCent;
            double currentUtilization = sumU - nextSumU;
            if(currentUtilization > 1){
                return false;
            }
            if(isDistant){
                distantUtilizations.add(currentUtilization / 2.);
            }
            else{
                localUtilizations.add(currentUtilization);
            }
            sumU = nextSumU;
        }
        if(sumU > 1){
            return false;
        }
        boolean isDistant = random.nextDouble() > localPerCent;
        if(isDistant){
            distantUtilizations.add(sumU / 2.);
        }
        else{
            localUtilizations.add(sumU);
        }
        return true;
    }

    /**
     * Generates the GlobalNetwork according to the GlobalNetworkGenerator.
     * @param maxPeriod The maximum period of the task generated.
     * @param threshold The threshold of the total utilization.
     * @return A new GlobalNetwork randomly created.
     */
    public GlobalNetwork generateGlobalNetwork(int maxPeriod, double threshold){
        while(true) {
            ArrayList<CANNetwork> canNetworks = new ArrayList<>();
            boolean endLoop = false;
            ArrayList<Double> localUtilizations = null;
            ArrayList<Double> distantUtilizations = null;
            while (!endLoop) {
                localUtilizations = new ArrayList<>();
                distantUtilizations = new ArrayList<>();
                endLoop = generateUtilizations(localUtilizations, distantUtilizations);
            }
            TreeMap<Integer, List<Schedulable>> localTasks = null;
            HashMap<Integer, List<Map.Entry<Schedulable, Integer>>> distantTasks = null;
            endLoop = false;
            while (!endLoop) {
                localTasks = new TreeMap<>();
                distantTasks = new HashMap<>();
                endLoop = completeMaps(localTasks, distantTasks, localUtilizations, distantUtilizations, maxPeriod, threshold);
            }
            canNetworks.addAll(localTasks.entrySet().stream().map(entry -> new CANNetwork(entry.getValue())).collect(Collectors.toList()));
            distantTasks.forEach(
                    (integer, entries) -> entries
                            .forEach(schedulableIntegerEntry ->
                                    canNetworks.get(integer).addExternalSchedulable(schedulableIntegerEntry.getKey(),
                                            canNetworks.get(schedulableIntegerEntry.getValue()))));

            if(canNetworks.stream().allMatch(canNetwork -> canNetwork.totalUtilization() < 1)) {
//                System.out.println("canNetworks : ");
//                canNetworks.forEach(canNetwork -> {
//                    System.out.println(canNetwork);
//                    System.out.println("network utilization : " + canNetwork.totalUtilization());
//                });
                return new GlobalNetwork(canNetworks);
            }
        }
    }


    /**
     * Completes the maps.
     * @param localTasks The localTasks map to complete.
     * @param distantTasks The distantTasks map to complete.
     * @param localUtilizations The List of Double which represents the local utilizations.
     * @param distantUtilizations The list of Double which represents the distant utilizations.
     * @param maxPeriod The maximum period of a task.
     * @param threshold The threshold of the total utilization of the GlobalNetwork.
     * @return True if the map are completed correctly, false otherwise.
     */
    private boolean completeMaps(TreeMap<Integer, List<Schedulable>> localTasks, HashMap<Integer, List<Map.Entry<Schedulable, Integer>>> distantTasks,
                                 ArrayList<Double> localUtilizations, ArrayList<Double> distantUtilizations,
                                 long maxPeriod, double threshold){

        List<PeriodicTask> localPeriodicTasks;
        List<PeriodicTask> distantPeriodicTasks;
        double um = u * m;
        while(true){
            distantPeriodicTasks = generatePeriodicTask(new ArrayList<>(distantUtilizations), maxPeriod, 0);
            localPeriodicTasks = generatePeriodicTask(new ArrayList<>(localUtilizations), maxPeriod, distantPeriodicTasks.size());
            double totalUtilization = distantPeriodicTasks.stream().mapToDouble(task -> ((double) task.getWcet() / (double) task.getPeriod()) * 2.).sum() +
                    localPeriodicTasks.stream().mapToDouble(task -> (double) task.getWcet() / (double) task.getPeriod()).sum();
            if(totalUtilization <= um && (um - threshold <= totalUtilization && totalUtilization <= um + threshold)){
                break;
            }
        }

        for(PeriodicTask periodicTask : distantPeriodicTasks){
            int indexFrom = random.nextInt(m);
            localTasks.computeIfAbsent(indexFrom, integer -> new LinkedList<>());
            localTasks.get(indexFrom).add(periodicTask);
            int indexTo = (indexFrom + random.nextInt(m - 1) + 1) % m;
            distantTasks.computeIfAbsent(indexTo, integer -> new LinkedList<>());
            distantTasks.get(indexTo).add(new AbstractMap.SimpleEntry<>(periodicTask, indexFrom));
        }
        for(PeriodicTask periodicTask : localPeriodicTasks){
            int indexFrom = random.nextInt(m);
            localTasks.computeIfAbsent(indexFrom, integer -> new LinkedList<>());
            localTasks.get(indexFrom).add(periodicTask);
        }
        return localTasks.size() == m;
    }

    /**
     * Generates the periodic tasks.
     * @param utilization The array of utilization to generate tasks.
     * @param maxPeriod The maximum period of a task.
     * @param start The start of the name
     * @return A new list of PeriodicTasks.
     */
    private List<PeriodicTask> generatePeriodicTask(ArrayList<Double> utilization, long maxPeriod, int start){
        List<PeriodicTask> tasks = new LinkedList<>();
         random.longs(utilization.size(), 1, maxPeriod).forEach(period -> {
            long wcet = Math.max((long) Math.floor(utilization.remove(0) * (double) period), 1);
            int nbTask = tasks.size() + 1 + start;
             tasks.add(new PeriodicTask(period, wcet, period, 0, "t" + nbTask));
        });
        return tasks;
    }
}
