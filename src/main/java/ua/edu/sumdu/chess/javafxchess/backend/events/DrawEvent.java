package ua.edu.sumdu.chess.javafxchess.backend.events;

import lombok.Getter;
import lombok.NonNull;

/**
 * Represents an event indicating a draw in the game.
 */
@Getter
public class DrawEvent {
    private final DrawReason reason;

    /**
     * Constructs a DrawEvent with the specified reason.
     *
     * @param reason The reason for the draw.
     */
    public DrawEvent(@NonNull DrawReason reason) {
        this.reason = reason;
    }
}
