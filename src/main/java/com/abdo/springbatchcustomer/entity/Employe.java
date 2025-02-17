package com.abdo.springbatchcustomer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@XmlRootElement(name = "employe")
public class Employe {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;

    private String name;

    private String email;

    private String phone;

    private double salary;

    @XmlElement
    public Integer getId() { return id; }

    @XmlElement
    public String getName() { return name; }

    @XmlElement
    public String getEmail() { return email; }

    @XmlElement
    public String getPhone() { return phone; }

    @XmlElement
    public Double getSalary() { return salary; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSalary(Double salary) { this.salary = salary; }
}
