package com.abdo.springbatchcustomer.config.Processors;

import com.abdo.springbatchcustomer.entity.Employe;
import com.abdo.springbatchcustomer.entity.EmployeDTO;
import org.springframework.batch.item.ItemProcessor;

public class CsvProcessor implements ItemProcessor<Employe, EmployeDTO> {
    @Override
    public EmployeDTO process(Employe employe) throws Exception {
        double prime = employe.getSalary() * 0.10; // Prime de 10%
        double taxe = (employe.getSalary() + prime) * 0.15; // Taxe de 15%
        EmployeDTO employeDTO = new EmployeDTO();
        employeDTO.setId(employe.getId());
        employeDTO.setName(employe.getName());
        employeDTO.setEmail(employe.getEmail());
        employeDTO.setPhone(employe.getPhone());
        employeDTO.setSalary(employe.getSalary());
        employeDTO.setSalaryAfterPrime(employe.getSalary() + prime);
        employeDTO.setSalaryAfterTax(employeDTO.getSalaryAfterPrime() - taxe);
        return employeDTO;
    }
}
