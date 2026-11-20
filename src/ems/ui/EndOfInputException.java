package ems.ui;

/** Raised when standard input closes (for example Ctrl-D) so the menus stop instead of looping. */
final class EndOfInputException extends RuntimeException {
    EndOfInputException() { super("Input ended"); }
}
