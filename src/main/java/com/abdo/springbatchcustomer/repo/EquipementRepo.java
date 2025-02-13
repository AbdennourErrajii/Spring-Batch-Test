package com.abdo.springbatchcustomer.repo;

import com.abdo.springbatchcustomer.entity.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementRepo extends JpaRepository<Equipement, Long> {
}
