package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TimeUpdatedEvent {
    private final int timeLeft;
    private final PieceColor currentColor;

    public TimeUpdatedEvent(int timeLeft, @NonNull PieceColor currentColor) {
        if (timeLeft < 0) {
            throw new IllegalArgumentException("timeLeft cannot be negative");
        }

        this.timeLeft = timeLeft;
        this.currentColor = currentColor;
    }
}
