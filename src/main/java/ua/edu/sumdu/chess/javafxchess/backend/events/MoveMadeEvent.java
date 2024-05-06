package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents an event indicating that a move has been made in the game.
 */
@Getter
public class MoveMadeEvent {
    private final PieceColor currentColor;

    /**
     * Constructs a MoveMadeEvent with the specified current color.
     *
     * @param currentColor The color of the player who made the move.
     */
    public MoveMadeEvent(@NonNull PieceColor currentColor) {
        this.currentColor = currentColor;
    }
}
