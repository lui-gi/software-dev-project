package ems.ui;

import ems.db.*;
import ems.model.User;
import ems.service.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.function.Supplier;

/** Test helper: drives the real menus with scripted keystrokes and returns everything printed. */
public final class UiScripts {
    private UiScripts() { }

    public static String hr(Supplier<Connection> src, User user, String keys) {
        return run(src, user, keys, true);
    }

    public static String employee(Supplier<Connection> src, User user, String keys) {
        return run(src, user, keys, false);
    }

    private static String run(Supplier<Connection> src, User user, String keys, boolean hr) {
        java.io.InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream((keys + "\n0\n").getBytes()));
            System.setOut(new PrintStream(buf, true));
            Console con = new Console();
            var dao = new EmployeeDao(src);
            var employees = new EmployeeService(dao);
            var reports = new ReportService(new PayrollDao(src), new ReportDao(src), dao);
            if (hr) new HrMenu(con, employees, new SalaryService(dao), reports).run(user);
            else new EmployeeMenu(con, employees, reports).run(user);
        } catch (EndOfInputException ignored) {
            // script ended
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }
        return buf.toString();
    }

}
