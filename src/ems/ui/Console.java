package ems.ui;

import java.util.Scanner;

/** Thin wrapper over standard input and output so menus stay short. */
final class Console {
    private final Scanner in = new Scanner(System.in);

    /** When input is piped from a file (not typed), echo each answer so a saved run reads like a real session. */
    private final boolean echo = !isInteractive();

    String ask(String label) {
        String answer = read(label);
        if (echo) System.out.println(answer);
        return answer;
    }

    private String read(String label) {
        System.out.print(label + ": ");
        if (!in.hasNextLine()) throw new EndOfInputException();
        return in.nextLine().trim();
    }

    /** Hides typing when run from a real terminal. */
    String askPassword(String label) {
        java.io.Console c = System.console();
        if (isInteractive()) {
            char[] pw = c.readPassword(label + ": ");
            if (pw == null) throw new EndOfInputException();
            return new String(pw);
        }
        String pw = read(label);
        if (echo) System.out.println("********");
        return pw;
    }

    private static boolean isInteractive() {
        java.io.Console c = System.console();
        return c != null && isTerminal(c);
    }

    /** Java 22+ returns a Console even for piped input; Console.isTerminal() tells them apart (older Java: always a terminal). */
    private static boolean isTerminal(java.io.Console c) {
        try { return (boolean) java.io.Console.class.getMethod("isTerminal").invoke(c); }
        catch (ReflectiveOperationException e) { return true; }
    }

    boolean confirm(String question) {
        return ask(question + " (Y/N)").equalsIgnoreCase("Y");
    }

    void say(String text) { System.out.println(text); }
}
