package tech.alexberbo.berboapp.service;

import org.springframework.data.domain.Page;
import tech.alexberbo.berboapp.model.Customer;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Customer getCustomer(Long id);
    Page<Customer> getCustomers(int page, int size);
    Page<Customer> searchCustomers(String name, int page, int size);
    Iterable<Customer> getCustomers();
}
