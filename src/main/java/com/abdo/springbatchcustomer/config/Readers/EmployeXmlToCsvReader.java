package com.abdo.springbatchcustomer.config.Readers;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class EmployeXmlToCsvReader extends StaxEventItemReader<Employe> {
    public EmployeXmlToCsvReader() {
        this.setResource(new FileSystemResource("src/main/resources/outputs/employe.xml"));
        this.setFragmentRootElementName("employe");
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Employe.class);
        this.setUnmarshaller(marshaller);
    }
}
