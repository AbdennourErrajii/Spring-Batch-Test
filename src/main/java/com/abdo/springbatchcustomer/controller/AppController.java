package com.abdo.springbatchcustomer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppController implements AppInterface{
    private final JobLauncher jobLauncher;

    private final Job runJob;
    private final Job runJob2;
    private final Job runJob3;
    private final Job runJob4;
    private final Job runJob5;


    @Override
    @PostMapping("/employee")
    public void CsvToDb() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt",System.currentTimeMillis())
                    .toJobParameters();
            this.jobLauncher.run(runJob,jobParameters);

        }catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                JobParametersInvalidException | JobRestartException e){
            e.printStackTrace();
        }

    }

    @Override
    @PostMapping("/departments")
    public void XMLToDb() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt",System.currentTimeMillis())
                    .toJobParameters();
            this.jobLauncher.run(runJob2,jobParameters);
        }catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                JobParametersInvalidException | JobRestartException e){
            e.printStackTrace();
        }


    }

    @Override
    @PostMapping("/equipements")
    public void JsonToDb() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt",System.currentTimeMillis())
                    .toJobParameters();
            this.jobLauncher.run(runJob3,jobParameters);
        }catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                JobParametersInvalidException | JobRestartException e){
            e.printStackTrace();
        }

    }

    @Override
    @PostMapping("/revenues")
    public void ExcelToDb() {
        try {
            System.out.println("Excel to DB");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            this.jobLauncher.run(runJob4, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void PdfToDb() {

    }

    @Override

    public void DbToCsv() {


    }

    @Override
    public void DbToXml() {

    }

    @Override
    public void DbToJson() {

    }

    @Override
    public void DbToExcel() {

    }

    @Override
    @PostMapping("/pdf")
    public void DbToPdf() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            this.jobLauncher.run(runJob5, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();

        }


    }
}
