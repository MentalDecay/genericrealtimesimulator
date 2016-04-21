/*
package grts.core.processor.policies.offline;

import grts.core.architecture.Architecture;
import grts.core.architecture.Processor;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Job;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LPDPM_safe implements IProcessorPolicy{

    private class Interval{
        private final long from;
        private final long to;
        private final long size;

        private Interval(long from, long to) {
            this.from = from;
            this.to = to;
            size = to - from;
        }

        @Override
        public String toString() {
            return "Interval from : " + from + " to : " + to;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Interval)){
                return false;
            }
            Interval interval = (Interval) obj;
            return from == interval.from && to == interval.to;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 31 + Float.floatToIntBits(from);
            hash = hash * 31 + Float.floatToIntBits(to);
            return hash;
        }
    }

    private final Architecture architecture;
    private final TreeMap<Interval, HashMap<Job, Double>> intervalToJobWeight = new TreeMap<>((o1, o2) -> {
        return Long.compare(o1.from, o2.from);
    });
    private boolean schedulableTaskSet = true;
    private final TreeMap<Long, Long> startToEnd = new TreeMap<>();
    private final HashMap<Job, List<Interval>> jobsIntervalMap = new HashMap<>();
    private final HashMap<Interval, List<Job>> activatedJobsByIntervalMap = new HashMap<>();
//    private final HashMap<Job, Integer> jobProcessorIdMap = new HashMap<>();
    private int processorLastIdle = -1;
    private final HashMap<Job, Job> jobsToRealJobs = new HashMap<>();
    private final long time;
    private final List<Job> executingJobs = new LinkedList<>();
    private final List<Integer> alreadyExecutingProcessors = new LinkedList<>();

    public LPDPM_safe(Architecture architecture, TaskSet taskSet, long time) {
        if (!taskSet.isRecurrent()) {
            throw new IllegalArgumentException("Can't use this algorithm with a non recurrent task set");
        }
        this.architecture = architecture;
        this.time = time;
        init(this.initTaskSet(taskSet.copy()));
        if (!schedulableTaskSet) {
            throw new IllegalArgumentException("Can't schedule this task set with this heuristic");
        }
    }

    private void init(TaskSet taskSet){
        boolean stop = false;
        TreeMap<Interval, List<Job>> intervalJobsMap = new TreeMap<>((o1, o2) -> {
            return Long.compare(o1.from, o2.from);
        });
        TreeSet<Long> intervals = new TreeSet<>();

//      Adds to intervals every beginning of intervals. Duplicates elements are not duplicated.
//      For instance, if there are 3 tasks which periods of 20, 20 and 30, intervals will contain [0, 20, 30, 40, 60].
        for(long i = 0; !stop; i++){
            Stream<AbstractRecurrentTask> recurrentTaskStream = taskSet.stream().map(schedulable -> (AbstractRecurrentTask) schedulable);
            long cmpt = i;
            List<Long> list = recurrentTaskStream
                    .map(abstractRecurrentTask -> abstractRecurrentTask.getMinimumInterArrivalTime() * cmpt)
                    .filter(aLong -> aLong <= time)
                    .collect(Collectors.toList());
            if(list.isEmpty()){
                stop = true;
            }
            intervals.addAll(list);
        }

//      Completes the startToEnd map. This map contains the intervals with the start and the end.
//      For instance, with the previous example this map contains { 0 => 20, 20 => 30, 30 => 40, 40 => 60}
        int maxLoop = intervals.size() - 1;
        for(int i = 0; i < maxLoop; i++){
            long start = intervals.pollFirst();
            long end = intervals.first();
            startToEnd.put(start, end);
        }

//      Creates the list of jobs which will execute between 0 and the time.
//        TODO Add check if the first jobs are after the timer and throw an exception in this case.
        LinkedList<Job> jobs = new LinkedList<>();
        Stream<AbstractRecurrentTask> recurrentTaskStream = taskSet.stream().map(schedulable -> (AbstractRecurrentTask) schedulable);
        recurrentTaskStream.forEach(abstractRecurrentTask -> {
            long jobActivationTimer = 0;
            jobs.add(abstractRecurrentTask.getFirstJob());
            while (jobActivationTimer < time) {
                Job job = abstractRecurrentTask.getRealNextJob(jobActivationTimer);
                if (job.getActivationTime() < time) {
                    jobs.add(job);
                }
                jobActivationTimer = job.getActivationTime() + job.getTaskInterArrivalTime();
            }
        });
        jobs.forEach(job -> completeMaps(job, intervalJobsMap, jobsIntervalMap));

//        TODO Use the intervalToJobWeight to compute the weight of each job with the LP.
//        Set with the hand to test.

        setValues(jobsIntervalMap);
    }

    private long computeSlackWCET(TaskSet taskSet, long time){
        double u =  taskSet
                .stream()
                .mapToDouble(schedulable -> {
            AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
            return (double) task.getWcet() / (double) task.getMinimumInterArrivalTime();
        })
                .sum();
        double utilizationSlack = architecture.getProcessors().length - u;
        return Math.round(utilizationSlack * time);
    }

    private void setValues(HashMap<Job, List<Interval>> jobsIntervalMap){
        Interval i1 = new Interval(0, 20);
        Interval i2 = new Interval(20, 30);
        Interval i3 = new Interval(30, 40);
        Interval i4 = new Interval(40, 60);
        intervalToJobWeight.put(i1, new HashMap<>());
        intervalToJobWeight.put(i2, new HashMap<>());
        intervalToJobWeight.put(i3, new HashMap<>());
        intervalToJobWeight.put(i4, new HashMap<>());
        jobsIntervalMap.forEach((job, intervals) -> {
            if (job.getTask().getName().equals("t1") && job.getJobId() == 1) {
                intervalToJobWeight.get(i1).put(job, 0.2);
            }
            if (job.getTask().getName().equals("t2") && job.getJobId() == 1) {
                intervalToJobWeight.get(i1).put(job, 0.6);
            }
            if (job.getTask().getName().equals("t3") && job.getJobId() == 1) {
                intervalToJobWeight.get(i1).put(job, 1.);
                intervalToJobWeight.get(i2).put(job, 0.);
            }
            if (job.getTask().getName().equals("t1") && job.getJobId() == 2) {
                intervalToJobWeight.get(i2).put(job, 0.4);
                intervalToJobWeight.get(i3).put(job, 0.);
            }
            if (job.getTask().getName().equals("t2") && job.getJobId() == 2) {
                intervalToJobWeight.get(i2).put(job, 0.6);
                intervalToJobWeight.get(i3).put(job, 0.6);
            }
            if (job.getTask().getName().equals("t3") && job.getJobId() == 2) {
                intervalToJobWeight.get(i3).put(job, 0.4);
                intervalToJobWeight.get(i4).put(job, 0.8);
            }
            if (job.getTask().getName().equals("t1") && job.getJobId() == 3) {
                intervalToJobWeight.get(i4).put(job, 0.2);
            }
            if (job.getTask().getName().equals("t2") && job.getJobId() == 3) {
                intervalToJobWeight.get(i4).put(job, 0.6);
            }
            if (job.getTask().getName().equals("slack") && job.getJobId() == 1) {
                intervalToJobWeight.get(i1).put(job, 0.2);
                intervalToJobWeight.get(i2).put(job, 1.);
                intervalToJobWeight.get(i3).put(job, 1.);
                intervalToJobWeight.get(i4).put(job, 0.4);
            }
        });

    }

    private void completeMaps(Job job, TreeMap<Interval, List<Job>> intervalJobsMap, HashMap<Job, List<Interval>> jobsIntervalMap){
        long start = job.getActivationTime();
        long stop = startToEnd.get(start);
        long jobEnd = job.getTaskInterArrivalTime() + job.getActivationTime();
        while(stop <= jobEnd){
            Interval interval = new Interval(start, stop);
            intervalJobsMap.computeIfAbsent(interval, interval1 -> new LinkedList<>());
            intervalJobsMap.get(interval).add(job);
            jobsIntervalMap.computeIfAbsent(job, job1 -> new LinkedList<>());
            jobsIntervalMap.get(job).add(interval);
            start = stop;
            if(!startToEnd.containsKey(start)){
                break;
            }
            stop = startToEnd.get(start);
        }
    }

    @Override
    public Processor[] getProcessors() {
        return architecture.getProcessors();
    }


    @Override
    public List<AbstractMap.SimpleEntry<Job, Integer>> chooseNextJobs(long time) {
        List<AbstractMap.SimpleEntry<Job, Integer>> entryList = new LinkedList<>();
        Interval interval = getInterval(time);
        List<Job> tmp = new ArrayList<>(activatedJobsByIntervalMap.get(interval));
        if(!tmp.stream().filter(job -> job.getTask().getName().equals("slack")).findFirst().isPresent()){
            processorLastIdle = -1;
        }
        List<Job> jobs = tmp.stream()
                .filter(job -> !executingJobs.contains(job))
                .sorted((o1, o2) -> Long.compare(o2.getExecutionTime(), o1.getExecutionTime()))
                .collect(Collectors.toList());
//        List<Integer> alreadyExecutingProcessors = new LinkedList<>();
//      Remember : if slack task scheduled on the end last time => schedule slack first;
//      If slack isn't schedule in first it's in last and remember where.
//      If a task has no laxity, schedule it.
//      Else schedule the first m tasks with the biggest weight
        Optional<Job> slackOptional = jobs.stream()
                .filter(job -> job.getTask().getName().equals("slack"))
                .findFirst();
        if(slackOptional.isPresent()){
            if(processorLastIdle != -1){
                entryList.add(new AbstractMap.SimpleEntry<>(slackOptional.get(), processorLastIdle));
                jobs.remove(slackOptional.get());
                executingJobs.add(slackOptional.get());
                alreadyExecutingProcessors.add(processorLastIdle);
                int nbProcessors = getProcessors().length - 1;
//              Check laxity.
                completeEntries(time, entryList, jobs, nbProcessors);
                if(interval.size != slackOptional.get().getExecutionTime()){
                    processorLastIdle = -1;
                }
            }
            else{
                int nbProcessors = getProcessors().length;
                if(jobs.size() > 1) {
                    jobs.remove(slackOptional.get());
                }
                completeEntries(time, entryList, jobs, nbProcessors);
            }
        }
        else{
            completeEntries(time, entryList, jobs, getProcessors().length);
        }
        return entryList;
    }

    private void completeEntries(long time, List<AbstractMap.SimpleEntry<Job, Integer>> entryList, List<Job> jobs, int nbProcessors) {
        List<Job> sortedList = jobs.stream()
                .sorted((o1, o2) -> Long.compare(o2.getRemainingTime(), o1.getRemainingTime()))
                .collect(Collectors.toList());
        List<Job> jobsWithNoLaxity = new LinkedList<>();
        while(!sortedList.isEmpty()){
            Job firstJob = sortedList.remove(0);
            long laxity = firstJob.getLaxity(time);
            if(laxity == 0){
                jobsWithNoLaxity.add(firstJob);
            }
            else{
                break;
            }
        }
        if(jobsWithNoLaxity.size() > nbProcessors){
            throw new IllegalArgumentException("Too much jobs with no laxity for too few processors");
        }
        for(Processor processor : getProcessors()){
            if(processor.getId() == processorLastIdle || alreadyExecutingProcessors.contains(Integer.valueOf(processor.getId()))){
                nbProcessors--;
                continue;
            }
            if(jobsWithNoLaxity.size() == 0){
                break;
            }
            Job job = jobsWithNoLaxity.remove(0);
            entryList.add(new AbstractMap.SimpleEntry<>(job, processor.getId()));
            if(job.getTask().getName().equals("slack")){
                processorLastIdle = processor.getId();
            }
            executingJobs.add(job);
            jobs.remove(job);
            alreadyExecutingProcessors.add(processor.getId());
            nbProcessors--;
        }
        if(jobsWithNoLaxity.size() != 0){
            System.err.println("JobsWithNoLaxity list is not empty");
            return;
        }
        int lastProcessor = 0;
        while(jobs.size() != 0 && nbProcessors > 0){
            lastProcessor = findFirstIdleProcessor(entryList, jobs, lastProcessor);
            nbProcessors--;
        }
    }

    private int findFirstIdleProcessor(List<AbstractMap.SimpleEntry<Job, Integer>> entryList, List<Job> jobs, int lastProcessor) {
        boolean notDone = true;
        while(notDone){
            if(alreadyExecutingProcessors.contains(lastProcessor)){
                lastProcessor++;
                continue;
            }
            Job job = jobs.remove(0);
            entryList.add(new AbstractMap.SimpleEntry<>(job, lastProcessor));
            if(job.getTask().getName().equals("slack")){
                processorLastIdle = lastProcessor;
            }
            executingJobs.add(job);
            alreadyExecutingProcessors.add(lastProcessor);
            lastProcessor++;
            notDone = false;
        }
        return lastProcessor;
    }

    @Override
    public void deleteActiveJob(Job job) {
        Interval interval = getInterval(job.getActivationTime());
        if(!activatedJobsByIntervalMap.get(interval).remove(job)){
            System.err.println("The job isn't in the activated jobs list");
        }
    }

    @Override
    public void addActiveJob(Job job) {
        List<Interval> intervals = jobsIntervalMap.get(job);
        if(intervals == null){
            throw new IllegalArgumentException("The job isn't computed by LPDPM_safe");
        }
        intervals.forEach(interval -> {
            long wcet = Math.round(intervalToJobWeight.get(interval).get(job) * interval.size);
            if(wcet > 0) {
                activatedJobsByIntervalMap.computeIfAbsent(interval, interval1 -> new LinkedList<>());
                Job jobToAdd = new Job(interval.from, interval.to, job.getJobId(),
                        wcet, job.getTask());
                activatedJobsByIntervalMap.get(interval).add(jobToAdd);
                jobsToRealJobs.put(jobToAdd, job);
            }
        });
    }

    @Override
    public void stopJobExecution(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.stopExecutionJob();
//        jobProcessorIdMap.remove(job, processorId);
        jobsToRealJobs.get(job).execute(job.getExecutionTime());
        if(!executingJobs.remove(job)){
            throw new IllegalArgumentException("This job is not executing");
        }
        alreadyExecutingProcessors.remove(Integer.valueOf(processorId));
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.executeJob(job);
//        jobProcessorIdMap.put(job, processorId);
    }

    @Override
    public Job getExecutingJob(int processorId) {
        return architecture.getProcessors()[processorId].getExecutingJob();
    }

    @Override
    public TaskSet initTaskSet(TaskSet taskSet) {
        List<Schedulable> schedulables = taskSet.stream().collect(Collectors.toList());
        schedulables.add(new PeriodicTask(time, computeSlackWCET(taskSet, time), time, 0, "slack"));
        return new TaskSet(schedulables);
    }

    private Interval getInterval(long time){
        if(startToEnd.containsKey(time)){
            return new Interval(time, startToEnd.get(time));
        }
        Optional<Interval> interval = startToEnd.entrySet().stream()
                .filter(longLongEntry -> longLongEntry.getKey() <= time && time <= longLongEntry.getValue())
                .map(longLongEntry1 -> new Interval(longLongEntry1.getKey(), longLongEntry1.getValue()))
                .findFirst();
        if(!interval.isPresent()){
            throw new IllegalArgumentException("Can't find an interval for this time : " + time);
        }
        return interval.get();
    }
}
*/
