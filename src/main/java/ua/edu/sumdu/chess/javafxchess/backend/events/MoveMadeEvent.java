package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MoveMadeEvent {
    private final PieceColor currentColor;

    public MoveMadeEvent(@NonNull PieceColor currentColor) {
        this.currentColor = currentColor;
    }
}
