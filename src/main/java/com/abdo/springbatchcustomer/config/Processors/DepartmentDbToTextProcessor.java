package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Department;
import org.springframework.batch.item.ItemProcessor;

public class DepartmentDbToTextProcessor implements ItemProcessor<Department, Department> {
    @Override
    public Department process(Department employe) throws Exception {
        return employe;
    }
}
