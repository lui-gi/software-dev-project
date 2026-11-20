package ems.db;

import ems.model.PayStatement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PayrollDao {
    private final Supplier<Connection> source;

    public PayrollDao(Supplier<Connection> source) { this.source = source; }

    /** Pay statements for one employee, most recent pay date first. */
    public List<PayStatement> history(int empID) {
        try (Connection c = source.get();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT pay_date, earnings, taxes FROM payroll WHERE empID = ? ORDER BY pay_date DESC, payID DESC")) {
            ps.setInt(1, empID);
            List<PayStatement> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new PayStatement(rs.getDate(1).toLocalDate(), rs.getBigDecimal(2), rs.getBigDecimal(3)));
            }
            return out;
        } catch (SQLException e) {
            throw SqlSupport.translate(e);
        }
    }
}
