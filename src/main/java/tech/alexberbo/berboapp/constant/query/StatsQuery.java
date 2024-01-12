package tech.alexberbo.berboapp.constant.query;

public interface StatsQuery {
    String STATS_QUERY = "SELECT c.total_customers, i.total_invoices, inv.total_billed FROM (SELECT COUNT(*) total_customers FROM customer) c, (SELECT COUNT(*) total_invoices FROM invoice) i, (SELECT ROUND(SUM(total)) total_billed FROM invoice) inv";
}
