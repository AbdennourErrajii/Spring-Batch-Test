package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.config.Readers.ApachePoiReader;
import com.abdo.springbatchcustomer.config.Readers.PoijiReader;
import com.abdo.springbatchcustomer.entity.Revenue;
import com.abdo.springbatchcustomer.repo.RevenueRepo;
import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@Configuration
@AllArgsConstructor
public class BatchConfigForExcel {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RevenueRepo revenueRepo;

//
@Bean
public ItemReader<Revenue> excelReader() {
    AbstractItemCountingItemStreamItemReader<Revenue> reader = new AbstractItemCountingItemStreamItemReader<Revenue>() {
        private Iterator<Row> rowIterator;
        @Override
        protected void doOpen() throws Exception {
            FileInputStream file = new FileInputStream(new File("src/main/resources/revenue.xlsx"));
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            rowIterator = sheet.iterator();
            rowIterator.next(); // Skip header
        }
        @Override
        protected Revenue doRead() {
            if (rowIterator != null && rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Revenue revenue = new Revenue();
                revenue.setYear(row.getCell(0).getNumericCellValue());
                revenue.setQuarter(row.getCell(1).getStringCellValue());
                revenue.setAmount(row.getCell(2).getStringCellValue());
                return revenue;
            }
            return null;
        }
        @Override
        protected void doClose() {}
    };
    reader.setName("excelReader");
    return reader;
}


    @Bean
    public ItemWriter<Revenue> revenueWriter() {
        return revenues -> {
            revenueRepo.saveAll(revenues);
        };
    }

    @Bean
    public ItemProcessor<Revenue, Revenue> revenueProcessor() {
        return revenue -> {
            revenue.setAmount(String.valueOf(Double.parseDouble(revenue.getAmount()) * 1.1));
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
                .<Revenue, Revenue>chunk(5, transactionManager)
                .reader(reader)
                .processor(revenueProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job runJob4() throws Exception {
        var builder = new JobBuilder("runJob4", jobRepository);
        return builder
                .start(stepExcelToDB(excelReader(), revenueWriter(), jobRepository, transactionManager))
                .build();
    }
}
