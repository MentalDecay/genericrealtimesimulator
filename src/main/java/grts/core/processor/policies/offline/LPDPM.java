package grts.core.processor.policies.offline;

import grts.core.architecture.Architecture;
import grts.core.architecture.Processor;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Job;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LPDPM implements IProcessorPolicy{

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
    private int processorLastIdle = -1;
    private final HashMap<Job, Job> jobsToRealJobs = new HashMap<>();
    private final long time;
    private final List<Job> executingJobs = new LinkedList<>();
    private final ArrayList<Boolean> alreadyExecutingProcessors;

    public LPDPM(Architecture architecture, TaskSet taskSet, long time) {
        if (!taskSet.isRecurrent()) {
            throw new IllegalArgumentException("Can't use this algorithm with a non recurrent task set");
        }
        this.architecture = architecture;
        this.time = time;
        init(this.initTaskSet(taskSet.copy()));
        if (!schedulableTaskSet) {
            throw new IllegalArgumentException("Can't schedule this task set with this heuristic");
        }
        alreadyExecutingProcessors = new ArrayList<>(Collections.nCopies(getProcessors().length, false));
    }

    /**
     * Creates the LP and computes it to initialize the LPDPM.
     * @param taskSet The tasks set with the slack task.
     */
    private void init(TaskSet taskSet){
        boolean stop = false;
        TreeMap<Interval, List<Job>> intervalJobsMap = new TreeMap<>((o1, o2) -> {
            return Long.compare(o1.from, o2.from);
        });
        TreeSet<Long> intervals = new TreeSet<>();

        //      Adds to the tree set every beginning of intervals. Duplicates elements are not duplicated.
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

        LinkedList<Job> jobs = createListOfJobs(taskSet);
        jobs.forEach(job -> completeMaps(job, intervalJobsMap));

//        setValues(jobsIntervalMap);
        createLP(intervalJobsMap);
    }

    private IloCplex createLP(TreeMap<Interval, List<Job>> intervalJobsMap){
        IloCplex model;
        class Pair {
            private final Interval interval;
            private final Job job;

            Pair(Interval interval, Job job) {
                this.interval = interval;
                this.job = job;
            }

            @Override
            public boolean equals(Object obj) {
                if(!(obj instanceof Pair)){
                    return false;
                }
                Pair pair = (Pair) obj;
                return interval.equals(pair.interval) && job.equals(pair.job);
            }

            @Override
            public int hashCode() {
                int hash = 1;
                hash = hash * 31 + interval.hashCode();
                hash = hash * 31 + job.hashCode();
                return hash;
            }

            @Override
            public String toString() {
                return interval.toString() + " " + job;
            }
        }

        HashMap<Pair, IloNumVar> variables = new HashMap<>();
        LinkedList<IloNumVar> wk = new LinkedList<>();
        ArrayList<IloNumVar> fk = new ArrayList<>();
        ArrayList<IloNumVar> ek = new ArrayList<>();
        ArrayList<IloNumVar> fck = new ArrayList<>();
        ArrayList<IloNumVar> eck = new ArrayList<>();
        try {
            model = new IloCplex();
            int nbInterval = 0;
            for(Map.Entry<Interval, List<Job>> entry : intervalJobsMap.entrySet()){
                nbInterval++;
                IloNumExpr expr = model.numExpr();
                for(Job job : entry.getValue()){
//                    IloNumVar var = model.numVar(0, Double.MAX_VALUE);
                    IloNumVar var = model.numVar(0, Double.MAX_VALUE, "w("+job.getJobId()+","+job.getTask().getName()+"),I"+nbInterval);
                    variables.put(new Pair(entry.getKey(), job), var);
                    if(job.getTask().getName().equals("slack")){
                        wk.add(var);
                    }
                    expr = model.sum(expr, var);
                }
                model.add(model.le(expr, getProcessors().length));
            }
            //          Second constraint : 0 <= w(j,k) <= 1
            for(IloNumVar var : variables.values()){
                model.add(model.ge(var, 0));
                model.add(model.le(var, 1));
            }
            //          Third constraint : foreach job : sum(w(j,k) * |Ik|) = executionTime(j)
            for(Map.Entry<Job, List<Interval>> entry : jobsIntervalMap.entrySet()){
                Job job = entry.getKey();
                IloNumExpr expr = model.numExpr();
                for(Interval interval : entry.getValue()){
                    expr = model.sum(expr, model.prod(variables.get(new Pair(interval, job)), interval.size));
                }
                model.add(model.eq(job.getExecutionTime(), expr));
            }

            //          Fourth constraint : fk and ek
            int nbFk = 0;
            for(IloNumVar var : wk){
                nbFk++;
                IloNumVar fkVar = model.boolVar("f" + nbFk);
                IloNumVar ekVar = model.boolVar("e" + nbFk);
                fk.add(fkVar);
                ek.add(ekVar);
                model.add(model.ifThen(model.eq(var, 1), model.eq(fkVar, 0)));
                model.add(model.ifThen(model.not(model.eq(var, 1)), model.eq(fkVar, 1)));
                model.add(model.ifThen(model.eq(var, 0), model.eq(ekVar, 0)));
                model.add(model.ifThen(model.not(model.eq(var, 0)), model.eq(ekVar, 1)));
            }
//            fck and eck constraints
            IloNumExpr expr = model.numExpr();
            for(int i = 0; i < wk.size() -1; i++){
                IloNumVar fckVar = model.boolVar("fc" + (i+1));
                IloNumVar eckVar = model.boolVar("ec" + (i+1));
                fck.add(fckVar);
                eck.add(eckVar);
                model.add(model.ifThen(model.and(model.eq(fk.get(i), 1), model.eq(fk.get(i + 1), 0)), model.eq(fckVar, 1)));
                model.add(model.ifThen(model.not(model.and(model.eq(fk.get(i), 1), model.eq(fk.get(i + 1), 0))), model.eq(fckVar, 0)));
                model.add(model.ifThen(model.and(model.eq(ek.get(i), 1), model.eq(ek.get(i + 1), 0)), model.eq(eckVar, 1)));
                model.add(model.ifThen(model.not(model.and(model.eq(ek.get(i), 1), model.eq(ek.get(i + 1), 0))), model.eq(eckVar, 0)));

                expr = model.sum(expr, model.sum(fk.get(i), ek.get(i), fck.get(i), eck.get(i)));
            }
            expr = model.sum(expr, model.sum(fk.get(fk.size()-1), ek.get(ek.size()-1)));
            model.addMinimize(expr);

//            System.out.println("solving ...");
//            System.out.println(model.getModel());
            if(!model.solve()){
                System.err.println("No solution for this problem");
                return null;
            }
//            System.out.println("Solution : ");
            for(Map.Entry<Pair, IloNumVar> entry : variables.entrySet()){
//                System.out.println(entry.getKey());
//                System.out.println(model.getValue(entry.getValue()));
                intervalToJobWeight.computeIfAbsent(entry.getKey().interval, interval -> new HashMap<>());
                intervalToJobWeight.get(entry.getKey().interval).put(entry.getKey().job, model.getValue(entry.getValue()));
            }

        } catch (IloException e) {
            System.err.println("Error with the cplex");
            e.printStackTrace();
            return null;
        }
        return model;
    }

    /**
     * Creates a list of jobs which will be scheduled by the LPDPM.
     * @param taskSet The tasks set from where the jobs will be created.
     * @return A list of jobs.
     */
    private LinkedList<Job> createListOfJobs(TaskSet taskSet) {
        LinkedList<Job> jobs = new LinkedList<>();
        Stream<AbstractRecurrentTask> recurrentTaskStream = taskSet.stream().map(schedulable -> (AbstractRecurrentTask) schedulable);
        recurrentTaskStream.forEach(abstractRecurrentTask -> {
            long jobActivationTimer = 0;
            Job firstJob = abstractRecurrentTask.getFirstJob();
            if(firstJob.getActivationTime() > time){
                throw new IllegalArgumentException("The first job of " + firstJob + "has an activation time higher than the time of LPDPM.");
            }
            jobs.add(firstJob);
            while (jobActivationTimer < time) {
                Job job = abstractRecurrentTask.getRealNextJob(jobActivationTimer);
                if (job.getActivationTime() < time) {
                    jobs.add(job);
                }
                jobActivationTimer = job.getActivationTime() + job.getTaskInterArrivalTime();
            }
        });
        return jobs;
    }

    /**
     * Computes the wcet of the slack task according to the other tasks.
     * @param taskSet The tasks set from where the wcet should be computed.
     * @param time The time of the LPDPM.
     * @return The wcet of the time (long).
     */
    private long computeSlackWCET(TaskSet taskSet, long time){
        double u =  taskSet
                .stream()
                .filter(schedulable -> !schedulable.getName().equals("slack"))
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

    /**
     * Completes the map of the LDPM
     * @param job
     * @param intervalJobsMap
     */
    private void completeMaps(Job job, TreeMap<Interval, List<Job>> intervalJobsMap){
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


        List<Job> tmp = new LinkedList<>(activatedJobsByIntervalMap.get(interval));
        //      If there is no "slack" job in this interval, the processorLastIdle is set to -1.
        if(!tmp.stream().anyMatch(job1 -> job1.getTask().getName().equals("slack"))){
            processorLastIdle = -1;
        }
        List<Job> jobs = new LinkedList<>();
        LinkedList<Job> slackJob = new LinkedList<>();
        //      The jobs are sorted by execution time, and for each job which is executing it is added to the jobs list if it's not a slack job, or to the slack job list otherwise.
        tmp.stream()
                .filter(job -> ! executingJobs.contains(job))
                .sorted((o1, o2) -> Long.compare(o2.getExecutionTime(), o1.getExecutionTime()))
                .forEach(job1 -> {
                    if (job1.getTask().getName().equals("slack")) {
                        slackJob.add(job1);
                    } else {
                        jobs.add(job1);
                    }
                });
        if(slackJob.size() > 1){
            throw new IllegalArgumentException("Multiple slack jobs in this interval");
        }
        Optional<Job> slackOptional = Optional.ofNullable(slackJob.peekFirst());


        if(slackOptional.isPresent()){
            if(processorLastIdle != -1){
                entryList.add(new AbstractMap.SimpleEntry<>(slackOptional.get(), processorLastIdle));
                executingJobs.add(slackOptional.get());
                alreadyExecutingProcessors.set(processorLastIdle, true);
                int nbProcessors = getProcessors().length;
                completeEntries(time, entryList, jobs, nbProcessors);
                if(interval.size != slackOptional.get().getExecutionTime()){
                    processorLastIdle = -1;
                }
            }
            else{
                int nbProcessors = getProcessors().length;
                if(slackOptional.get().getLaxity(time) == 0){
                    jobs.add(slackOptional.get());
                }
                completeEntries(time, entryList, jobs, nbProcessors);
            }
        }
        else{
            completeEntries(time, entryList, jobs, getProcessors().length);
        }

        return entryList;
    }

    /**
     * Adds a job to the entryList.
     * @param time The maximum time of LPDPM.
     * @param entryList The entryList.
     * @param jobs The list of jobs.
     * @param nbProcessors The number of processors which are idle.
     */
    private void completeEntries(long time, List<AbstractMap.SimpleEntry<Job, Integer>> entryList, List<Job> jobs, int nbProcessors) {
        List<Job> sortedList = jobs.stream()
                .sorted((o1, o2) -> Long.compare(o2.getRemainingTime(), o1.getRemainingTime()))
                .collect(Collectors.toList());
        List<Job> jobsWithNoLaxity = new LinkedList<>();
        //      Adds the jobs with a laxity of 0 to the jobsWithNoLaxity list.
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
        int lastProcessor = 0;
        for(Processor processor : getProcessors()){
//            if(jobsWithNoLaxity.size() == 0){
//                break;
//            }
            if(processor.getId() == processorLastIdle || alreadyExecutingProcessors.get(processor.getId())){
                nbProcessors--;
                continue;
            }
            if(jobsWithNoLaxity.size() != 0) {
                Job job = jobsWithNoLaxity.remove(0);
                entryList.add(new AbstractMap.SimpleEntry<>(job, processor.getId()));
                if (job.getTask().getName().equals("slack")) {
                    processorLastIdle = processor.getId();
                }
                executingJobs.add(job);
                jobs.remove(job);
                alreadyExecutingProcessors.set(processor.getId(), true);
                nbProcessors--;
                lastProcessor = processor.getId();
            }
        }
        if(jobsWithNoLaxity.size() != 0){
            System.err.println("JobsWithNoLaxity list is not empty");
            return;
        }
        while(jobs.size() != 0 && nbProcessors > 0){
            lastProcessor = addsToFirstIdleProcessor(entryList, jobs, lastProcessor);
            nbProcessors--;
        }
    }

    /**
     * Adds a job to the first idle processor.
     * @param entryList The entryList to add the job to the processor.
     * @param jobs The list of jobs.
     * @param firstProcessorToCheck The id of first processor to inspect.
     * @return
     */
    private int addsToFirstIdleProcessor(List<AbstractMap.SimpleEntry<Job, Integer>> entryList, List<Job> jobs, int firstProcessorToCheck) {
        while(true){
            if(alreadyExecutingProcessors.get(firstProcessorToCheck)){
                firstProcessorToCheck++;
                continue;
            }
            Job job = jobs.remove(0);
            entryList.add(new AbstractMap.SimpleEntry<>(job, firstProcessorToCheck));
            if(job.getTask().getName().equals("slack")){
                processorLastIdle = firstProcessorToCheck;
            }
            executingJobs.add(job);
            alreadyExecutingProcessors.set(firstProcessorToCheck, true);
            firstProcessorToCheck++;
            return firstProcessorToCheck;
        }
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
            throw new IllegalArgumentException("The job isn't computed by LPDPM");
        }
        intervals.forEach(interval -> {
            long wcet = Math.round(intervalToJobWeight.get(interval).get(job) * interval.size);
            if (wcet > 0) {
                activatedJobsByIntervalMap.computeIfAbsent(interval, interval1 -> new LinkedList<>());
                Job jobToAdd = new Job(interval.from, interval.to, job.getJobId(),
                        wcet, job.getTask());
                if (jobToAdd.getTask().getName().equals("slack")) {
                }
                activatedJobsByIntervalMap.get(interval).add(jobToAdd);
                jobsToRealJobs.put(jobToAdd, job);
            }
        });
    }

    @Override
    public void stopJobExecution(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.stopExecutionJob();
        jobsToRealJobs.get(job).execute(job.getExecutionTime());
        if(!executingJobs.remove(job)){
            throw new IllegalArgumentException("This job is not executing");
        }
        alreadyExecutingProcessors.set(processorId, false);
    }

    @Override
    public void executeJob(Job job, int processorId) {
        Processor processor = architecture.getProcessors()[processorId];
        processor.executeJob(job);
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

    /**
     * Get the interval according to the time.
     * @param time The time of the interval.
     * @return The interval which contains the time.
     */
    private Interval getInterval(long time){
        if(startToEnd.containsKey(time)){
            return new Interval(time, startToEnd.get(time));
        }
        //        TODO Use the TreeMap properties to have a faster estimation of the key.
        for(Map.Entry<Long, Long> entry : startToEnd.entrySet()){
            if(entry.getKey() <= time && time <= entry.getValue()){
                return new Interval(entry.getKey(), entry.getValue());
            }
        }
        throw new IllegalArgumentException("Can't find an interval for this time : " + time);
    }
}
