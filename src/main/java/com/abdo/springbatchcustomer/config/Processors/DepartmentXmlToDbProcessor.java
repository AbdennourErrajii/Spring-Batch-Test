package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Department;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DepartmentXmlToDbProcessor implements ItemProcessor<Department, Department> {
    @Override
    public Department process(Department department) throws Exception {
        return department;
    }
}
