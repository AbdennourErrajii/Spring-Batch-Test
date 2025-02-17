package com.abdo.springbatchcustomer.entity;
;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.XmlElement;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "department")
public class Department {
        @Id
        //@GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;
        private double numberEmploye;

      @XmlElement(name = "id")
      public Integer getId() {
        return id;
      }
      public void setId(Integer id) {
        this.id = id;
      }
    @XmlElement(name = "name")
      public String getName() {
        return name;
      }
        public void setName(String name) {
            this.name = name;
        }
        @XmlElement(name = "numberEmploye")
        public double getNumberEmploye() {
            return numberEmploye;
        }
        public void setNumberEmploye(double numberEmploye) {
            this.numberEmploye = numberEmploye;
        }

}
