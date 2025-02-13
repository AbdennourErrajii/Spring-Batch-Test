package com.abdo.springbatchcustomer.repo;

import com.abdo.springbatchcustomer.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Long> {

}
