package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents an event indicating that a player has won the game.
 */
@Getter
public class WinEvent {
    private final WinReason reason;
    private final PieceColor winner;

    /**
     * Constructs a WinEvent with the specified reason
     * and winning player color.
     *
     * @param reason The reason for the win.
     * @param winner The color of the winning player.
     */
    public WinEvent(@NonNull WinReason reason, @NonNull PieceColor winner) {
        this.reason = reason;
        this.winner = winner;
    }
}
