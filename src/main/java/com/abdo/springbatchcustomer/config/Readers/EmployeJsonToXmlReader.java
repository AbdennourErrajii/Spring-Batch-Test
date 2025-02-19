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
    private final Iterator<Employe> employeIterator;
    public EmployeJsonToXmlReader() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("src/main/resources/outputs/employe.json");
        // Vérifier si le fichier existe, sinon le créer avec un tableau JSON vide
        if (!jsonFile.exists()) {
            jsonFile.getParentFile().mkdirs(); // Créer les dossiers parents si nécessaire
            Files.write(Paths.get(jsonFile.getPath()), "[]".getBytes()); // Créer un fichier JSON vide
        }
        List<Employe> employes = Arrays.asList(objectMapper.readValue(jsonFile, Employe[].class));
        this.employeIterator = employes.iterator();
    }
    @Override
    public Employe read() throws Exception {
        return employeIterator.hasNext() ? employeIterator.next() : null;
    }
}
