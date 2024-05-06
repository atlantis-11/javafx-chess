package ua.edu.sumdu.chess.javafxchess.exceptions;

/**
 * Represents an exception thrown when the input is invalid.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
