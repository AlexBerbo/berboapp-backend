package tech.alexberbo.berboapp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.model.Customer;
import tech.alexberbo.berboapp.model.Stats;
import tech.alexberbo.berboapp.repository.CustomerRepository;
import tech.alexberbo.berboapp.rowmapper.StatsRowMapper;
import tech.alexberbo.berboapp.service.CustomerService;

import java.util.Date;
import java.util.Map;

import static org.springframework.data.domain.PageRequest.of;
import static tech.alexberbo.berboapp.constant.query.StatsQuery.STATS_QUERY;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) { return customerRepository.save(customer); }

    @Override
    public Customer getCustomer(Long id) { return customerRepository.findById(id).get(); }

    @Override
    public Page<Customer> getCustomers(int page, int size) { return customerRepository.findAll(of(page, size)); }

    @Override
    public Page<Customer> searchCustomers(String name, int page, int size) {
        return customerRepository.findByNameContaining(name, of(page, size));
    }

    @Override
    public Iterable<Customer> getCustomers() { return customerRepository.findAll(); }

    @Override
    public Stats getStats() {
        return jdbc.queryForObject(STATS_QUERY, Map.of(), new StatsRowMapper());
    }
}
