package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.ItemProcessor;

public class EmployeHtmlProcessor implements ItemProcessor<Employe, Employe> {
    @Override
    public Employe process(Employe employe) throws Exception {
        employe.setSalary(employe.getSalary() * 1.1);
        employe.setName(employe.getName().toLowerCase()); // Exemple de transformation
        return employe;
    }
}
