package com.abdo.springbatchcustomer.config.Readers;
import com.abdo.springbatchcustomer.entity.Employe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
public class EmployeJsonToXmlReader implements ItemReader<Employe> {
    private Iterator<Employe> employeIterator;

    @Override
    public Employe read() throws Exception {
        if (employeIterator == null) { // Lire les données une seule fois
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File("src/main/resources/outputs/employe.json");
            List<Employe> employes = Arrays.asList(objectMapper.readValue(jsonFile, Employe[].class));
            employeIterator = employes.iterator();
        }
        return employeIterator.hasNext() ? employeIterator.next() : null;
    }
}
