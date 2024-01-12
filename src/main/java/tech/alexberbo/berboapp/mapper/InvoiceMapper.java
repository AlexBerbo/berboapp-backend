package tech.alexberbo.berboapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.alexberbo.berboapp.dto.InvoiceDTO;
import tech.alexberbo.berboapp.model.Invoice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "customer", source = "invoices.customer")
    @Mapping(target = "serviceCustomer", source = "invoices.serviceCustomer")
    List<InvoiceDTO> invoiceToDTO(List<Invoice> invoices);
}
