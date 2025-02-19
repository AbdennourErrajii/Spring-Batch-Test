package com.abdo.springbatchcustomer.config.Readers;
import com.abdo.springbatchcustomer.entity.Department;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


public class DepartmentXmlToDbReader extends StaxEventItemReader<Department> {
    public DepartmentXmlToDbReader() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Department.class);
        setName("departmentReader");
        setResource(new ClassPathResource("inputs/department.xml"));
        setFragmentRootElementName("department");
        setUnmarshaller(unmarshaller);
        setStrict(false);
    }
}
