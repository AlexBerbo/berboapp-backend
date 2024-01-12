package tech.alexberbo.berboapp.service;

import org.springframework.data.domain.Page;
import tech.alexberbo.berboapp.model.ServiceCustomer;

import java.util.List;

public interface ServiceCustomerService {
    List<ServiceCustomer> getServices();

    Page<ServiceCustomer> services(int page, int size);

    ServiceCustomer createService(ServiceCustomer serviceCustomer);

    ServiceCustomer updateService(ServiceCustomer serviceCustomer);

    ServiceCustomer getService(Long id);

    void deleteService(Long id);
}
