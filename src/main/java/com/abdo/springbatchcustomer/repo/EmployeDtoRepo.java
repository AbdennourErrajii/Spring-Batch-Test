package com.abdo.springbatchcustomer.repo;

import com.abdo.springbatchcustomer.entity.EmployeDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeDtoRepo extends JpaRepository<EmployeDTO, Integer> {

}
