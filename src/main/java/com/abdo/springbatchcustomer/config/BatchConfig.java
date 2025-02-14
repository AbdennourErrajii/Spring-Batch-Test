package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.config.Processors.CsvProcessor;
import com.abdo.springbatchcustomer.config.Processors.EmployeHtmlProcessor;
import com.abdo.springbatchcustomer.config.Processors.EmployeProcessor;
import com.abdo.springbatchcustomer.config.Writers.EmployeHtmlWriter;
import com.abdo.springbatchcustomer.entity.Employe;

import com.abdo.springbatchcustomer.entity.EmployeDTO;
import com.abdo.springbatchcustomer.repo.EmployeRepo;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;



@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final EmployeRepo employeRepo;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;


    // -----------Step 1--------------------
    @Bean
    public Step step1() {
        return new StepBuilder("EmployeCSVImport",jobRepository)
                .<Employe,Employe>chunk(10,transactionManager)
                .reader(itemReader())
                .writer(empWriter())
                .processor(empProcessor())
                //.faultTolerant()  // Rend le Step tolérant aux erreurs
                //.skipPolicy(new EmployeSkipPolicy())  // Ignore les erreurs définies
                .build();
    }
    @Bean
    public FlatFileItemReader<Employe> itemReader() {
        FlatFileItemReader<Employe> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/employe.csv"));
        reader.setName("EmployeeCSVReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
    }

    @Bean
    public EmployeProcessor empProcessor() {
        return new EmployeProcessor();
    }
    @Bean
    public RepositoryItemWriter<Employe> empWriter() {
        RepositoryItemWriter<Employe> writer = new RepositoryItemWriter<>();
        writer.setRepository(employeRepo);
        writer.setMethodName("save");
        return writer;

    }

    // -----------Step 2--------------------
    @Bean
    public Step step2() {
        return new StepBuilder("EmployeHTMLGeneration", jobRepository)
                .<Employe, Employe>chunk(10, transactionManager)
                .reader(databaseItemReader()) // Lire depuis la base
                .processor(employeHtmlProcessor()) // Transformer
                .writer(employeHtmlWriter()) // Générer la page HTML
                .build();
    }
    @Bean
    public EmployeHtmlProcessor employeHtmlProcessor() {
        return new EmployeHtmlProcessor();
    }

    @Bean
    public EmployeHtmlWriter employeHtmlWriter() {
        return new EmployeHtmlWriter();
    }



    @Bean
    public JpaPagingItemReader<Employe> databaseItemReader() {
        JpaPagingItemReader<Employe> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM Employe e"); // Lire tous les employés
        reader.setPageSize(10);
        return reader;
    }

    // -----------------Step 3-------------------------
    @Bean
    public Step step3() {
        return new StepBuilder("GenerateNewTable", jobRepository)
                .<Employe, EmployeDTO>chunk(10, transactionManager)
                .reader(itemReader()) // Lire depuis la base
                .processor(csvProcessor()) // Transformer si besoin
                .writer(newTableWriter()) // Générer la page HTML
                .build();
    }
    @Bean
    public   NewTableWriter newTableWriter() {
        return new NewTableWriter();
    }

    @Bean
    public CsvProcessor csvProcessor(){
        return new CsvProcessor();
    }


    //-----------------------------------------------

    @Bean
    public Job  runJob() throws Exception {
        Flow flow1 = new FlowBuilder<SimpleFlow>("flow1")
                .start(step1())
                .next(step2())
                .build();
        Flow flow2 = new FlowBuilder<SimpleFlow>("flow2")
                .start(step3())
                .build();
        return new JobBuilder("ImportEmployeJob", jobRepository)
                .start(flow1)
                .split(new SimpleAsyncTaskExecutor()).add(flow2)
                .end()
                .build();
    }

    private LineMapper<Employe> lineMapper() {
        DefaultLineMapper<Employe> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "email", "phone","salary");// to mape it to the employe object
        BeanWrapperFieldSetMapper<Employe> fieldSetMapper = new BeanWrapperFieldSetMapper<>(); //automatically maps CSV fields (after tokenization) to Java object fields.
        fieldSetMapper.setTargetType(Employe.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
