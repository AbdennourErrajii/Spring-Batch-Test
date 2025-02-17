package com.abdo.springbatchcustomer.config;
import com.abdo.springbatchcustomer.entity.Employe;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;

@Configuration
@AllArgsConstructor
@Slf4j
public class BatchConfigForDbToPdf {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    @Bean
    public JpaCursorItemReader<Employe> employeeDbReader() {
        System.out.println("employeeDbReader");
        JpaCursorItemReader<Employe> reader = new JpaCursorItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM Employe e");
        System.out.println("reader");
        return reader;
    }

    @Bean
    public ItemProcessor<Employe, Employe> employeeProcessor() {
        return employee -> employee;
    }

    @Bean
    public ItemWriter<Employe> employeePdfWriter() {
        return items -> {
            // Create output directory
            File outputDir = new File("src/main/resources/outputs");
            if (!outputDir.exists()) {
                boolean created = outputDir.mkdirs();
                if (!created) {
                    throw new IOException("Could not create output directory");
                }
                log.info("Created output directory: {}", outputDir.getAbsolutePath());
            }

            // Create PDF file
            File pdfFile = new File(outputDir, "employes.pdf");
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up the content stream
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);

                // Write title
                contentStream.showText("Liste des Employés");
                contentStream.newLine();
                contentStream.newLine();

                // Write employee details
                for (Employe employe : items) {
                    contentStream.showText("- ID : " + employe.getId());
                    contentStream.newLine();
                    contentStream.showText("- Nom : " + employe.getName());
                    contentStream.newLine();
                    contentStream.showText("- Email : " + employe.getEmail());
                    contentStream.newLine();
                    contentStream.showText("- Téléphone : " + employe.getPhone());
                    contentStream.newLine();
                    contentStream.showText("- Salaire : " + employe.getSalary() + "€");
                    contentStream.newLine();
                    contentStream.newLine();
                }
                contentStream.endText();
            }
            try {
                document.save(pdfFile);
                log.info("PDF generated successfully at: {}", pdfFile.getAbsolutePath());
            } finally {
                document.close();
            }
        };
    }
    @Bean
    public Step generatePdfStep() {
        return new StepBuilder("generatePdfStep", jobRepository)
                .<Employe, Employe>chunk(10, transactionManager)
                .reader(employeeDbReader())
                .processor(employeeProcessor())
                .writer(employeePdfWriter())
                .build();
    }

    @Bean
    public Job runJob5() {
        return new JobBuilder("runJob5", jobRepository)
                .start(generatePdfStep())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        jobExecution.getExecutionContext().put("startTime", System.currentTimeMillis());
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        long startTime = jobExecution.getExecutionContext().getLong("startTime");
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        System.out.println("Job execution time: " + duration + " ms");
                    }
                })
                .build();

    }
}
