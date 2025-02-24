package com.abdo.springbatchcustomer.config.Writers;
import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


public class EmployeJsonToXmlWriter extends StaxEventItemWriter<Employe> {
    public EmployeJsonToXmlWriter() {
        // Configurer le marshaller pour transformer Employe en XML
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Employe.class);
        setMarshaller(marshaller);
        setResource(new FileSystemResource("src/main/resources/outputs/employe.xml"));
        // Configurer l'encodage et la version XML
        setOverwriteOutput(true);
        setRootTagName("employes"); // Racine du fichier XML
        setSaveState(true);
    }
}
