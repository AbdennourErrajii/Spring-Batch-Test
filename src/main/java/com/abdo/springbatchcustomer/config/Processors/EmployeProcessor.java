package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.ItemProcessor;

public class EmployeProcessor implements ItemProcessor<Employe, Employe> {
    @Override
    public Employe process(Employe employe) {
        employe.setName(employe.getName().toUpperCase());

        if (!employe.getEmail().contains("@") || employe.getSalary() < (5000)) {
            //throw new IllegalArgumentException("***********\nInvalid email: ***********\n" + employe.getEmail());
            return null;
        }

        return employe;
    }
}
