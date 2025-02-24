package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DepartmentXmlToDbProcessor implements ItemProcessor<Department, Department> {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentXmlToDbProcessor.class);
    @Override
    public Department process(Department department) throws Exception {
        logger.info("Processing Department ID: {} in thread: {}", department.getId(), Thread.currentThread().getName());
        return department;
    }
}
