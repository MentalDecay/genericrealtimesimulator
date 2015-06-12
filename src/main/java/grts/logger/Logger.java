package grts.logger;

import grts.core.schedulable.Job;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Logger {
    private final BufferedWriter activationWriter;
    private final BufferedWriter executionWriter;

    public Logger(String path) throws IOException {
        activationWriter = Files.newBufferedWriter(Paths.get(path, "LogActivations").toAbsolutePath(), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        executionWriter = Files.newBufferedWriter(Paths.get(path, "LogExecutions").toAbsolutePath(), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    public Logger() throws IOException {
        activationWriter = Files.newBufferedWriter(Paths.get(".", "LogActivations"), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        executionWriter = Files.newBufferedWriter(Paths.get(".", "LogExecutions"), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    public void silentlyClose() {
        try {
            activationWriter.close();
        } catch (IOException e) {
            //Already closed, do nothing
        }
        try {
            executionWriter.close();
        } catch (IOException e) {
            //Already closed, do nothing
        }
    }

    public void writeJobActivation(List<Job> jobs, long time) {
        try {
            activationWriter.write("Time : " + time + "\n");
            for (Job job : jobs) {
                activationWriter.write("activating job : " + job.getJobId() + " from " + job.getTask().getName() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Can't write on the activation file.");
        }
    }

    public void writeJobExecution(Job jobToExecute, Job executingJob) {
        try {
            if (jobToExecute != null) {
                if (executingJob == null) {
                    executionWriter.write("New job executing : " + jobToExecute.getJobId() +
                            " from : " + jobToExecute.getTask().getName() + ".\n");
                } else if (jobToExecute != executingJob) {
                    executionWriter.write("Job (" + jobToExecute.getJobId() + ") from " + jobToExecute.getTask().getName() +
                            " is preempting job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() + "\n");
                    executionWriter.write("Job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() +
                            " stops. " + executingJob.getRemainingTime() + " unit(s) of time remaining.\n");
                } else {
                    executionWriter.write("Job (" + executingJob.getJobId() + ") from " + executingJob.getTask().getName() +
                            " continue its execution. " + executingJob.getRemainingTime() + " unit(s) of time remaining.\n");
                }
            }
            else{
                executionWriter.write("Nothing is executing at this time\n");
            }
        } catch (IOException e) {
            System.err.println("Can't write on the execution file.");
        }
    }

    public void writeEndExecution(Job job){
        try {
            executionWriter.write("Job (" + job.getJobId() + ") from " + job.getTask().getName() + " ended its execution.\n");
        } catch (IOException e) {
            System.err.println("Can't write on the execution file.");
        }
    }

    public void writeMissedDeadline(Job job){
        try {
            executionWriter.write("Job (" + job.getJobId() + ") from " + job.getTask().getName() + " just missed its deadline.\n");
        } catch (IOException e) {
            System.err.println("Can't write on the execution file.");
        }
    }

    public void writeTime(long time){
        try {
            executionWriter.write("Time : " + time + "\n");
        } catch (IOException e) {
            System.err.println("Can't write on the execution file.");
        }
    }



}
