package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents an event indicating that the time left
 * for a player has been updated.
 */
@Getter
public class TimeUpdatedEvent {
    private final int timeLeft;
    private final PieceColor currentColor;

    /**
     * Constructs a TimeUpdatedEvent with the specified time left and current color.
     *
     * @param timeLeft The time left for the player, in seconds.
     * @param currentColor The color of the player whose time has been updated.
     */
    public TimeUpdatedEvent(int timeLeft, @NonNull PieceColor currentColor) {
        if (timeLeft < 0) {
            throw new IllegalArgumentException("timeLeft cannot be negative");
        }

        this.timeLeft = timeLeft;
        this.currentColor = currentColor;
    }
}
