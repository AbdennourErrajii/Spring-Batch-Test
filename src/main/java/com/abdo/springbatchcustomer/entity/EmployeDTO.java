package com.abdo.springbatchcustomer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeDTO {
    @Id
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private double salary;
    private double salaryAfterPrime;
    private double salaryAfterTax;
}
