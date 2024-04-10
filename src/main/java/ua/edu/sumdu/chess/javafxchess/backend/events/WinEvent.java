package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class WinEvent {
    private final WinReason reason;
    private final PieceColor winner;

    public WinEvent(@NonNull WinReason reason, @NonNull PieceColor winner) {
        this.reason = reason;
        this.winner = winner;
    }
}
