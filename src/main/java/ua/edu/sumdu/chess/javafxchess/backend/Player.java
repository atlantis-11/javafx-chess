package ua.edu.sumdu.chess.javafxchess.backend;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a player in the chess game.
 */
@Getter
public class Player {
    private final PieceColor pieceColor;
    @Setter
    private int timeLeft;

    /**
     * Constructs a player with the specified piece color.
     *
     * @param pieceColor The color of the player's pieces.
     */
    public Player(@NonNull PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    /**
     * Decrements the remaining time for the player by one second.
     */
    public void decrementTimeLeft() {
        timeLeft--;
    }
}
