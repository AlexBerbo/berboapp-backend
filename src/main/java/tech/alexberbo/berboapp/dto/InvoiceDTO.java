package tech.alexberbo.berboapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.alexberbo.berboapp.model.Customer;
import tech.alexberbo.berboapp.model.ServiceCustomer;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String status;
    private double total;
    private Date createdAt;
    private Customer customer;
    private ServiceCustomer serviceCustomer;
}
