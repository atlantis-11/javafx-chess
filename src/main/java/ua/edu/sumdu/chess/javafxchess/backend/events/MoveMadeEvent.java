package ua.edu.sumdu.chess.javafxchess.backend.events;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;

@Getter
public class MoveMadeEvent {
    Board board;
    PieceColor currentColor;

    public MoveMadeEvent(Board board, PieceColor currentColor) {
        this.board = board;
        this.currentColor = currentColor;
    }
}
