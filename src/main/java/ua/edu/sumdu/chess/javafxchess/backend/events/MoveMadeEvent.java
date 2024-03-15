package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;

@Getter
public class MoveMadeEvent {
    private final PieceColor currentColor;

    public MoveMadeEvent(PieceColor currentColor) {
        this.currentColor = currentColor;
    }
}
