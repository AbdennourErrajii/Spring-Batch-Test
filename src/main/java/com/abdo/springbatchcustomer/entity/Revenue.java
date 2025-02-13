package com.abdo.springbatchcustomer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Revenue {
    @Id
    private int year;
    private String quarter;
    private double amount;
}
