package com.abdo.springbatchcustomer.config.Readers;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public class EmployeCsvToExcelReader extends FlatFileItemReader<Employe> {
    public EmployeCsvToExcelReader() {
        setResource(new FileSystemResource("src/main/resources/outputs/employe.csv"));
        setLineMapper(new DefaultLineMapper<Employe>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("id", "name", "email", "salary");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employe>() {{
                setTargetType(Employe.class);
            }});
        }});
    }

}
