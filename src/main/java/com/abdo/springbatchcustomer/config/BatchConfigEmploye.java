package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.config.Processors.*;
import com.abdo.springbatchcustomer.config.Readers.EmployeJsonToXmlReader;
import com.abdo.springbatchcustomer.config.Readers.EmployeXmlToCsvReader;
import com.abdo.springbatchcustomer.config.Writers.EmployeDbToPdfWriter;
import com.abdo.springbatchcustomer.config.Writers.EmployeHtmlWriter;
import com.abdo.springbatchcustomer.config.Writers.EmployeJsonToXmlWriter;
import com.abdo.springbatchcustomer.config.Writers.EmployeXmlToCsvWriter;
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
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;



@Configuration
@RequiredArgsConstructor
public class BatchConfigEmploye {

    private final EmployeRepo employeRepo;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;


    // -----------Step 1 Csv To Db--------------------
    @Bean
    public Step step1() {
        return new StepBuilder("EmployeCSVImport",jobRepository)
                .<Employe,Employe>chunk(5,transactionManager)
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
        reader.setResource(new FileSystemResource("src/main/resources/inputs/employe.csv"));
        reader.setName("EmployeeCSVReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
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

    // -----------Step 2 Db To Html--------------------
    @Bean
    public Step step2() {
        return new StepBuilder("EmployeHTMLGeneration", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeDbItemReader()) // Lire depuis la base
                .processor(employeHtmlProcessor()) // Transformer
                .writer(employeHtmlWriter()) // Générer la page HTML
                .build();
    }
    @Bean
    public JpaPagingItemReader<Employe> employeDbItemReader() {
        JpaPagingItemReader<Employe> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM Employe e"); // Lire tous les employés
        reader.setPageSize(10);
        return reader;
    }
    @Bean
    public EmployeHtmlProcessor employeHtmlProcessor() {
        return new EmployeHtmlProcessor();
    }

    @Bean
    public EmployeHtmlWriter employeHtmlWriter() {
        return new EmployeHtmlWriter();
    }





    // -----------------Step 3 CSV To Db (Employe.CSV TO Employe DTO Db)-------------------------
    @Bean
    public Step step3() {
        return new StepBuilder("GenerateNewTable", jobRepository)
                .<Employe, EmployeDTO>chunk(5, transactionManager)
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

   /*---------------------------Step 4 Db To Json-----------------------  */

    @Bean
    public EmployeDbToJsonProcessor employeDbToJsonProcessor() {
        return new EmployeDbToJsonProcessor();
    }


    @Bean
    public JsonFileItemWriter<Employe> employeDbToJsonWriter() {
        return new JsonFileItemWriterBuilder<Employe>()
                .name("EmployeDbToJsonWriter")
                .resource(new FileSystemResource("src/main/resources/outputs/employe.json"))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }
    @Bean
    public Step step4() {
        return new StepBuilder("dbToJson", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeDbItemReader())
                .processor(employeDbToJsonProcessor())
                .writer(employeDbToJsonWriter())
                .build();
    }

    /*------------------------ Step 5 Db To Pdf  -----------------------  */


    public EmployeDbToPdfProcessor employeDbToPdfProcessor() {
        return new EmployeDbToPdfProcessor();
    }
    public EmployeDbToPdfWriter employeDbToPdfWriter() {
        return new EmployeDbToPdfWriter();
    }
    @Bean
    public Step step5() {
        return new StepBuilder("DbToPdfStep", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeDbItemReader())
                .processor(employeDbToPdfProcessor())
                .writer(employeDbToPdfWriter())
                .build();
    }

    /* ----------------------  Step 6 Json To XML ----------------------------  */

    public EmployeJsonToXmlReader employeJsonToXmlReader() throws Exception {
        return new EmployeJsonToXmlReader();
    }
    public EmployeJsonToXmlProcessor employeJsonToXmlProcessor() {
        return new EmployeJsonToXmlProcessor();
    }
    public EmployeJsonToXmlWriter employeJsonToXmlWriter() {
        return new EmployeJsonToXmlWriter();
    }
    @Bean
    public Step step6() throws Exception {
        return new StepBuilder("JsonToXmlStep", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeJsonToXmlReader())
                .processor(employeJsonToXmlProcessor())
                .writer(employeJsonToXmlWriter())
                .build();
    }

    /* ----------------------  Step 7 XML To CSV ----------------------------  */

    public EmployeXmlToCsvReader employeXmlToCsvReader() throws Exception {
        return new EmployeXmlToCsvReader();
    }
    public EmployeXmlToCsvProcessor employeXmlToCsvProcessor() {
        return new EmployeXmlToCsvProcessor();
    }
    public EmployeXmlToCsvWriter employeXmlToCsvWriter() {
        return new EmployeXmlToCsvWriter();
    }

    @Bean
    public Step step7() throws Exception {
        return new StepBuilder("XmlToCsvStep", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeXmlToCsvReader())
                .processor(employeXmlToCsvProcessor())
                .writer(employeXmlToCsvWriter())
                .build();
    }

    //-----------------------------------------------

    @Bean
    public Job  runJob1() throws Exception {
        Flow flow1 = new FlowBuilder<SimpleFlow>("flow1")
                .start(step1()) // CSV to DB
                .next(step2()) // DB to HTML
                .next(step4()) // Db to Json
                .next(step5()) // Db to PDF
                .next(step6()) // Json to XML
                .next(step7()) // XML to CSV
                .build();
        Flow flow2 = new FlowBuilder<SimpleFlow>("flow2")
                .start(step3()) // Employe en CSV to EmplyeDTO en Db
                .build();
        return new JobBuilder("runJob1", jobRepository)
                .start(flow1)
                .split(new SimpleAsyncTaskExecutor()).add(flow2)
                .end()
                .build();
    }


}
