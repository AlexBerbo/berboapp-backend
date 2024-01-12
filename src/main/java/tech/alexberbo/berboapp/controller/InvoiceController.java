package tech.alexberbo.berboapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.Invoice;
import tech.alexberbo.berboapp.report.InvoiceReport;
import tech.alexberbo.berboapp.service.CustomerService;
import tech.alexberbo.berboapp.service.InvoiceService;
import tech.alexberbo.berboapp.service.ServiceCustomerService;
import tech.alexberbo.berboapp.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController extends ExceptionHandling {
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final CustomerService customerService;
    private final ServiceCustomerService serviceCustomerService;

    @PostMapping("/create")
    ResponseEntity<HttpResponse> createInvoice(@AuthenticationPrincipal UserDTO user, @RequestBody Invoice invoice) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "invoice", invoiceService.createInvoice(invoice)))
                        .message("Invoice created!")
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping(value = "/list")
    ResponseEntity<HttpResponse> getInvoices(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, Optional<Integer> size) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "invoices", invoiceService.getInvoices(page.orElse(0), size.orElse(10))))
                        .message("Invoices retrieved!")
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping("/new")
    ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "customers", customerService.getCustomers(),
                                "services", serviceCustomerService.getServices()))
                        .message("Add new invoice!")
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    ResponseEntity<HttpResponse> getInvoice(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "invoice", invoice,
                                "customer", invoice.getCustomer()))
                        .message("User, invoice and customer!")
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @PostMapping("/add-to-customer/{customerId}/{serviceId}")
    ResponseEntity<HttpResponse> addInvoiceToCustomer(@AuthenticationPrincipal UserDTO user,
                                                      @PathVariable("customerId") Long customerId,
                                                      @PathVariable("serviceId") Long serviceId,
                                                      @RequestBody Invoice invoice) {
        invoiceService.addInvoiceToCustomer(customerId, serviceId, invoice);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "customers", customerService.getCustomers(),
                                "services", serviceCustomerService.getServices()))
                        .message(String.format("Invoice added to the Customer with id: %s", customerId))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping("/download/report")
    ResponseEntity<Resource> getInvoice() {
        List<Invoice> invoices = invoiceService.getAll();
        InvoiceReport report = new InvoiceReport(invoices);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-name", "invoice-report.xlsx");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-name=invoice-report.xlsx");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .headers(headers)
                .body(report.export());
    }
}
