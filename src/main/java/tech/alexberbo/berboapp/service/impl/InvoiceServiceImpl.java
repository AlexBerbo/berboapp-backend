package tech.alexberbo.berboapp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.model.Customer;
import tech.alexberbo.berboapp.model.Invoice;
import tech.alexberbo.berboapp.repository.CustomerRepository;
import tech.alexberbo.berboapp.repository.InvoiceRepository;
import tech.alexberbo.berboapp.service.InvoiceService;

import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.springframework.data.domain.PageRequest.of;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber(randomAlphanumeric(10).toUpperCase());
        invoice.setCreatedAt(new Date());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoices(int page, int size) {
        return invoiceRepository.findAll(of(page, size));
    }

    @Override
    public void addInvoiceToCustomer(Long customerId, Invoice invoice) {
        invoice.setInvoiceNumber(randomAlphanumeric(10).toUpperCase());
        Customer customer = customerRepository.findById(customerId).get();
        invoice.setCustomer(customer);
        invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).get();
    }
}
