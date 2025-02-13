package com.abdo.springbatchcustomer.repo;

import com.abdo.springbatchcustomer.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepo extends JpaRepository<Revenue, Integer> {
}
