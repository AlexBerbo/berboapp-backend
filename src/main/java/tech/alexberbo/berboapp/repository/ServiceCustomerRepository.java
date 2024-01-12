package tech.alexberbo.berboapp.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import tech.alexberbo.berboapp.model.ServiceCustomer;

@Repository
public interface ServiceCustomerRepository extends PagingAndSortingRepository<ServiceCustomer, Long>, ListCrudRepository<ServiceCustomer, Long> {
}
