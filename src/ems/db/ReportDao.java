package ems.db;

import ems.model.ReportRow;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Monthly pay totals grouped by job title or by division. */
public class ReportDao {
    private final Supplier<Connection> source;

    public ReportDao(Supplier<Connection> source) { this.source = source; }

    public List<ReportRow> payByJobTitle(YearMonth month) {
        return totals("SELECT jt.job_title, SUM(p.earnings) FROM payroll p "
            + "JOIN employee_job_titles ej ON ej.empID = p.empID JOIN job_titles jt ON jt.job_titleID = ej.job_titleID "
            + "WHERE p.pay_date >= ? AND p.pay_date < ? GROUP BY jt.job_title ORDER BY jt.job_title", month);
    }

    public List<ReportRow> payByDivision(YearMonth month) {
        return totals("SELECT d.name, SUM(p.earnings) FROM payroll p "
            + "JOIN employee_division ed ON ed.empID = p.empID JOIN division d ON d.divID = ed.divID "
            + "WHERE p.pay_date >= ? AND p.pay_date < ? GROUP BY d.name ORDER BY d.name", month);
    }

    private List<ReportRow> totals(String sql, YearMonth month) {
        try (Connection c = source.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(month.atDay(1)));
            ps.setDate(2, Date.valueOf(month.plusMonths(1).atDay(1)));
            List<ReportRow> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new ReportRow(rs.getString(1), rs.getBigDecimal(2)));
            }
            return out;
        } catch (SQLException e) {
            throw SqlSupport.translate(e);
        }
    }
}
