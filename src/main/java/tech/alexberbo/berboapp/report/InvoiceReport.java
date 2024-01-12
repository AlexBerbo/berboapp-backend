package tech.alexberbo.berboapp.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import tech.alexberbo.berboapp.exception.ApiException;
import tech.alexberbo.berboapp.model.Invoice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.util.stream.IntStream.range;

@Slf4j
public class InvoiceReport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Invoice> invoices;
    private static String[] HEADERS = { "ID", "Invoice Number", "Services", "Total", "Customer ID", "Customer Name", "Status", "Created At" };
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * Setting our variables and declaring the objects
     * */
    public InvoiceReport(List<Invoice> invoices) {
        this.invoices = invoices;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Invoices");
        setHeaders();
    }

    /**
     * It sets the header row wich will contain the ID, Name and so on and on
     * */
    private void setHeaders() {
        Row row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        cellStyle.setFont(font);
        range(0, HEADERS.length).forEach(index -> {
            Cell cell = row.createCell(index);
            cell.setCellValue(HEADERS[index]);
            cell.setCellStyle(cellStyle);
        });
    }

    /**
     * Exposes the generateReport method
     * */
    public InputStreamResource export() {
        return generateReport();
    }

    /**
     * Creating the cells, adding the style, and font.
     * For every invoice/customer a new row and cell is created and filled with invoice/customer data of that iteration.
     *
     * */
    private InputStreamResource generateReport() {
        // ByteArrayOutputStream i don't understand
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle cellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontHeight(10);
            cellStyle.setFont(font);
            int rowNumber = 1;
            for(Invoice invoice: invoices) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(invoice.getId());
                row.createCell(1).setCellValue(invoice.getInvoiceNumber());
                row.createCell(2).setCellValue(invoice.getServiceCustomer().getId());
                row.createCell(3).setCellValue(invoice.getServiceCustomer().getServiceCustomerNumber());
                row.createCell(4).setCellValue(invoice.getServiceCustomer().getName());
                row.createCell(5).setCellValue("$" + invoice.getTotal());
                row.createCell(6).setCellValue(invoice.getCustomer().getId());
                row.createCell(7).setCellValue(invoice.getCustomer().getName());
                row.createCell(8).setCellValue(invoice.getStatus());
                row.createCell(9).setCellValue(DateFormatUtils.format(invoice.getCreatedAt(), DATE_FORMAT));
            }
            workbook.write(out);
            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Unable to download report!");
        }
    }
}
