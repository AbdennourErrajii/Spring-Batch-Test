package com.abdo.springbatchcustomer.config.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import java.time.Duration;
import java.time.ZoneOffset;

public class DepartmentJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("********Before Job*************");
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        executionContext.put("Name", "Abdennour");
        executionContext.put("LastName", "Erraji");
        System.out.println("Job Name :"+jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters :"+jobExecution.getJobParameters());
        System.out.println("ExecutorContext :" + executionContext);
    }
    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("********** After Job ***********");
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        System.out.println("Job Name :"+jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters :"+jobExecution.getJobParameters());
        System.out.println("ExecutorContext :" + executionContext);
        System.out.println("Start Time: "+jobExecution.getStartTime());
        System.out.println("End Time: "+jobExecution.getEndTime());

        // Conversion en Instant avec UTC (ou autre ZoneId si nécessaire)
        Duration duration = Duration.between(jobExecution.getStartTime().toInstant(ZoneOffset.UTC), jobExecution.getEndTime().toInstant(ZoneOffset.UTC));
        // Affichage en millisecondes et secondes
        System.out.println("********Durée d'exécution :" + duration.toMillis() + " ms");
        System.out.println("********Durée d'exécution :" + duration.getSeconds() + " s");

    }
}
