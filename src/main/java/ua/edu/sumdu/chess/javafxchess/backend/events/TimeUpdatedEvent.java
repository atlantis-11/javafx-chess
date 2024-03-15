package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;

@Getter
public class TimeUpdatedEvent {
    private final int timeLeft;
    private final PieceColor currentColor;

    public TimeUpdatedEvent(int timeLeft, PieceColor currentColor) {
        this.timeLeft = timeLeft;
        this.currentColor = currentColor;
    }
}
