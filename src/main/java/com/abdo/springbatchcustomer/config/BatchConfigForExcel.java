package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.entity.Revenue;
import com.abdo.springbatchcustomer.repo.RevenueRepo;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Configuration
@AllArgsConstructor
public class BatchConfigForExcel {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RevenueRepo revenueRepo;

    @Bean
    public ItemReader<Revenue> revenueReader() throws IOException {
        List<Revenue> revenues = new ArrayList<>();
        InputStream file = new ClassPathResource("revenue.xlsx").getInputStream();
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0); // Première feuille

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Ignorer l'en-tête

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Revenue revenue = new Revenue();
            revenue.setYear((int) row.getCell(0).getNumericCellValue());
            revenue.setQuarter(row.getCell(1).getStringCellValue());
            revenue.setAmount(row.getCell(2).getNumericCellValue());
            revenues.add(revenue);
        }

        workbook.close();
        return new ListItemReader<>(revenues);
    }


    @Bean
    public ItemWriter<Revenue> revenueWriter() {
        return revenues -> revenueRepo.saveAll(revenues);
    }

    @Bean
    public ItemProcessor<Revenue, Revenue> revenueProcessor() {
        return revenue -> {
            revenue.setAmount(revenue.getAmount() * 1.1);
            return revenue;
        };
    }

    @Bean
    public Step stepExcelToDB(ItemReader<Revenue> reader,
                              ItemWriter<Revenue> writer,
                              JobRepository jobRepository,
                              PlatformTransactionManager transactionManager) {

        var builder = new StepBuilder("stepExcelToDB", jobRepository);
        return builder
                .<Revenue, Revenue>chunk(1, transactionManager)
                .reader(reader)
                .processor(revenueProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job runJob4() throws Exception {
        var builder = new JobBuilder("runJob4", jobRepository);
        return builder
                .start(stepExcelToDB(revenueReader(), revenueWriter(), jobRepository, transactionManager))
                .build();
    }
}
