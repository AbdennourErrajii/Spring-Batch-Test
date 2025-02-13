package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.entity.Department;
import com.abdo.springbatchcustomer.repo.DepartmentRepo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfigForXml {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DepartmentRepo departmentRepo;
    @Bean
    @StepScope
    public StaxEventItemReader departmentReader() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Department.class);
        return new StaxEventItemReaderBuilder<Department>()
                .name("departmentReader")
                .resource(new ClassPathResource("department.xml"))
                .addFragmentRootElements("department")
                .unmarshaller(unmarshaller)
                .build();
    }

    @Bean
    public ItemWriter<Department> departmentWriter() {
        return departments -> departmentRepo.saveAll(departments);
    }


    @Bean
    public ItemProcessor<Department, Department> departmentProcessor() {
        return department -> {
            department.setName(department.getName().toLowerCase());
            return department;
        };
    }

    @Bean
    public Step step11(StaxEventItemReader<Department> reader,
                       ItemWriter<Department> writer,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {

        var builder = new StepBuilder("step11", jobRepository);
        return builder
                .<Department, Department>chunk(1, transactionManager)
                .reader(reader)
                .processor(departmentProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job  runJob2() throws Exception {
        var builder = new JobBuilder("runJob2", jobRepository);
        return builder
                .start(step11(departmentReader(), departmentWriter(), jobRepository, transactionManager))
                .build();
    }

}
