package tech.alexberbo.berboapp.service;

import org.springframework.data.domain.Page;
import tech.alexberbo.berboapp.model.Invoice;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long customerId, Invoice invoice);
    Invoice getInvoice(Long id);
}
