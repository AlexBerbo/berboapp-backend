package tech.alexberbo.berboapp.service;

import org.springframework.data.domain.Page;
import tech.alexberbo.berboapp.model.Invoice;

import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long customerId, Long serviceId, Invoice invoice);
    Invoice getInvoice(Long id);
    List<Invoice> getAll();

}
