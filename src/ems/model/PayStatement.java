package ems.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayStatement(LocalDate payDate, BigDecimal earnings, BigDecimal taxes) {
    public BigDecimal net() { return earnings.subtract(taxes); }
}
