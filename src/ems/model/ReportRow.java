package ems.model;

import java.math.BigDecimal;

/** One line of a grouped report: a label (job title or division) and its total pay. */
public record ReportRow(String label, BigDecimal total) { }
