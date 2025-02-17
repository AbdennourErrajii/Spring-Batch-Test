package com.abdo.springbatchcustomer.config.Writers;
import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

public class EmployeXmlToCsvWriter extends FlatFileItemWriter<Employe> {
    public EmployeXmlToCsvWriter() {
        this.setResource(new FileSystemResource("src/main/resources/outputs/employe.csv"));
        DelimitedLineAggregator<Employe> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<Employe> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "email", "salary"});
        lineAggregator.setFieldExtractor(fieldExtractor);
        this.setLineAggregator(lineAggregator);
    }

}
