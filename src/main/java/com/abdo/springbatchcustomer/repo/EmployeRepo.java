package com.abdo.springbatchcustomer.repo;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeRepo extends JpaRepository<Employe, Long> {


}
