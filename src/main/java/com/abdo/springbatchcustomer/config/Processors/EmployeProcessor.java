package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Employe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class EmployeProcessor implements ItemProcessor<Employe, Employe> {
    private static final Logger logger = LoggerFactory.getLogger(EmployeProcessor.class);
    @Override
    public Employe process(Employe employe) {
       /* employe.setName(employe.getName().toUpperCase());
        if (!employe.getEmail().contains("@") || employe.getSalary() < 5000) {
            logger.warn("Employé invalide : Email = {}, Salaire = {}", employe.getEmail(), employe.getSalary());
            return null;
        } */

        return employe;
    }
}
