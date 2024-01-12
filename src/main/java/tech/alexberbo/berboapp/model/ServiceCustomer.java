package tech.alexberbo.berboapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class ServiceCustomer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String serviceCustomerNumber;
    private String name;
    private double price;
    private double fee;
    @OneToOne(mappedBy = "serviceCustomer", fetch = EAGER, cascade = ALL)
    private Invoice invoice;
}
