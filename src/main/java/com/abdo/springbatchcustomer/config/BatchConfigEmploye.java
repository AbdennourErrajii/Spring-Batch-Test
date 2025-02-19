package com.abdo.springbatchcustomer.config;
import com.abdo.springbatchcustomer.config.Processors.*;
import com.abdo.springbatchcustomer.config.Readers.EmployeCsvToExcelReader;
import com.abdo.springbatchcustomer.config.Readers.EmployeJsonToXmlReader;
import com.abdo.springbatchcustomer.config.Readers.EmployeXmlToCsvReader;
import com.abdo.springbatchcustomer.config.Readers.PartitionedCsvReader;
import com.abdo.springbatchcustomer.config.Writers.*;
import com.abdo.springbatchcustomer.config.partitioner.CsvPartitioner;
import com.abdo.springbatchcustomer.entity.Employe;
import com.abdo.springbatchcustomer.entity.EmployeDTO;
import com.abdo.springbatchcustomer.repo.EmployeRepo;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
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
    public Step step1Master() {
        return new StepBuilder("step1Master", jobRepository)
                .partitioner("EmployeCsvToDb", partitioner()) // Utiliser le partitioner
                .step(step1()) // Le step esclave
                .partitionHandler(partitionHandler()) // Gestionnaire de partitions
                .build();
    }
    @Bean
    public Step step1() {
        return new StepBuilder("EmployeCsvToDb", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(itemReader())
                .processor(empProcessor())
                .writer(empWriter())
                .build();
    }
    @Bean
    public Partitioner partitioner() {
        CsvPartitioner partitioner = new CsvPartitioner();
        partitioner.setResource(new FileSystemResource("src/main/resources/inputs/employe.csv"));
        return partitioner;
    }
    @Bean
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(10); // Nombre de partitions
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor()); // Exécution parallèle
        handler.setStep(step1()); // Le step esclave
        return handler;
    }
   @Bean
   @StepScope
   public PartitionedCsvReader itemReader() {
       PartitionedCsvReader reader = new PartitionedCsvReader();
       reader.setResource(new FileSystemResource("src/main/resources/inputs/employe.csv"));
       reader.setLineMapper(lineMapper());
       reader.setLinesToSkip(1);
       reader.setStrict(false);
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
    //@Bean
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

    //------------Step 8 : CSV To Excel --------------------

    public EmployeCsvToExcelReader employeCsvToExcelReader() throws Exception {
        return new EmployeCsvToExcelReader();
    }
    public EmployeCsvToExcelProcessor employeCsvToExcelProcessor() {
        return new EmployeCsvToExcelProcessor();
    }

    public EmployeCsvToExcelWriter employeCsvToExcelWriter() {
        return new EmployeCsvToExcelWriter();
    }
    @Bean
    public Step step8() throws Exception {
        return new StepBuilder("CsvToExcelStep", jobRepository)
                .<Employe, Employe>chunk(5, transactionManager)
                .reader(employeCsvToExcelReader())
                .processor(employeCsvToExcelProcessor())
                .writer(employeCsvToExcelWriter())
                .build();
    }


    //-----------------------------------------------


    @Bean
    public Job  runJob1() throws Exception {
        Flow flow1 = new FlowBuilder<SimpleFlow>("flow1")
                //.start(step1()) // CSV to DB
                .start(step1Master()) // CSV to DB
                .next(step2()) // Db to HTML
                .next(step4()) // Db to Json
                .next(step5()) // Db to PDF
                .build();
        Flow flow2 = new FlowBuilder<SimpleFlow>("flow2")
                .start(step3()) // Employe en CSV to EmplyeDTO en Db
                .build();
        Flow flow3 = new FlowBuilder<SimpleFlow>("flow3")
                .start(step6()) // Json to XML
                .next(step7()) // XML to CSV
                .next(step8()) // CSV to Excel
                .build();
        return new JobBuilder("runJob1", jobRepository)
                .start(flow1)
                .next(flow3)
                .split(new SimpleAsyncTaskExecutor()).add(flow2)
                .end()
                .build();
    }


}
