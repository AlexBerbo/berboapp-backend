package tech.alexberbo.berboapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.model.ServiceCustomer;
import tech.alexberbo.berboapp.repository.ServiceCustomerRepository;
import tech.alexberbo.berboapp.service.ServiceCustomerService;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.springframework.data.domain.PageRequest.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCustomerServiceImpl implements ServiceCustomerService {
    private final ServiceCustomerRepository serviceCustomerRepository;

    @Override
    public List<ServiceCustomer> getServices() {
        return serviceCustomerRepository.findAll();
    }

    @Override
    public Page<ServiceCustomer> services(int page, int size) {
        return serviceCustomerRepository.findAll(of(page, size));
    }

    @Override
    public ServiceCustomer createService(ServiceCustomer serviceCustomer) {
        serviceCustomer.setServiceCustomerNumber(randomAlphanumeric(10));
        return serviceCustomerRepository.save(serviceCustomer);
    }

    @Override
    public ServiceCustomer updateService(ServiceCustomer serviceCustomer) {
        return serviceCustomerRepository.save(serviceCustomer);
    }

    @Override
    public ServiceCustomer getService(Long id) {
        return serviceCustomerRepository.findById(id).get();
    }

    @Override
    public void deleteService(Long id) {
        serviceCustomerRepository.deleteById(id);
    }
}
