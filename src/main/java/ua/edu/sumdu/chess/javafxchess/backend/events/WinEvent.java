package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;

@Getter
public class WinEvent {
    WinReason reason;
    PieceColor winner;

    public WinEvent(WinReason reason, PieceColor winner) {
        this.reason = reason;
        this.winner = winner;
    }
}
