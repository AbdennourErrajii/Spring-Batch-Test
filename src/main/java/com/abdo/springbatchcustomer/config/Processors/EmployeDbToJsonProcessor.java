package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.ItemProcessor;

public class EmployeDbToJsonProcessor implements ItemProcessor<Employe, Employe> {
    @Override
    public Employe process(Employe employe) throws Exception {
        return employe;
    }
}
