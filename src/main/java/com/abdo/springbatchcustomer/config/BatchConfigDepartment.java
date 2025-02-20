package com.abdo.springbatchcustomer.config;
import com.abdo.springbatchcustomer.config.Processors.DepartmentDbToTextProcessor;
import com.abdo.springbatchcustomer.config.Processors.DepartmentXmlToDbProcessor;
import com.abdo.springbatchcustomer.config.Readers.DepartmentXmlToDbReader;
import com.abdo.springbatchcustomer.entity.Department;
import com.abdo.springbatchcustomer.repo.DepartmentRepo;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class BatchConfigDepartment {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DepartmentRepo departmentRepo;
    private final DataSource dataSource;
    //-----------------Step 1: Xml To DB-----------------
    @Bean
    public DepartmentXmlToDbProcessor departmentXmlToDbProcessor() {
        return new DepartmentXmlToDbProcessor();
    }
    @Bean
    public DepartmentXmlToDbReader departmentXmlToDbReader() {
        return new DepartmentXmlToDbReader();
    }
   /* @Bean
    public ItemWriter<Department> departmentXmlToDbWriter() {
        return departments -> departmentRepo.saveAll(departments);
    }*/
   @Bean
   public JdbcBatchItemWriter<Department> departmentXmlToDbWriter() {
       return new JdbcBatchItemWriterBuilder<Department>()
               .dataSource(dataSource)
               .sql("INSERT INTO department (name, number_employe) VALUES (?, ?)")
               .itemPreparedStatementSetter((department, ps) -> {
                   ps.setString(1, department.getName());
                   ps.setDouble(2, department.getNumberEmploye()); // Vérifie le type !
               })
               .build();
   }
    @Bean
    public Step stepD1() throws Exception {
        return new StepBuilder("DepartmentXmlToDbStep", jobRepository)
                .<Department,Department>chunk(5, transactionManager)
                .reader(departmentXmlToDbReader())
                .processor(departmentXmlToDbProcessor())
                .writer(departmentXmlToDbWriter())
                //.taskExecutor(taskExecutor())
                .build();
    }
    //-----------------Step 2: Db To Text-----------------
    @Bean
    public ItemReader<Department> departmentDbToTextReader() {
        JpaPagingItemReader<Department> reader = new JpaPagingItemReader<>();
        reader.setName("departmentDbReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT d FROM Department d");
        reader.setPageSize(5);
        return reader;
    }
    @Bean
    public DepartmentDbToTextProcessor departmentDbToTextProcessor() {
        return new DepartmentDbToTextProcessor();
    }

    @Bean
    public ItemWriter<Department> departmentDbToTextWriter() {
        return new FlatFileItemWriterBuilder<Department>()
                .name("departmentTextWriter")
                .resource(new FileSystemResource("outputs/department.txt"))
                .delimited()
                .names(new String[]{"id", "name","numberEmploye"}) // Map fields to columns
                .build();
    }
    @Bean
    public Step stepD2() throws Exception {
        return new StepBuilder("DepartmentDbToTextStep", jobRepository)
                .<Department,Department>chunk(5, transactionManager)
                .reader(departmentDbToTextReader())
                .processor(departmentDbToTextProcessor())
                .writer(departmentDbToTextWriter())
                .build();
    }
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(4);
        return executor;
    }

    @Bean
    public Job  runJob2() throws Exception {
        var builder = new JobBuilder("runJob2", jobRepository);
        return builder
                .start(stepD1()) //Xml To Db
                //.next(stepD2()) //Db To Text
                .build();
    }

}
