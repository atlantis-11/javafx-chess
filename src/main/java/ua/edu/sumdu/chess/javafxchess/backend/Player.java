package ua.edu.sumdu.chess.javafxchess.backend;

import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    private final PieceColor pieceColor;
    @Setter
    private int timeLeft;

    public Player(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public void decrementTimeLeft() {
        timeLeft--;
    }
}
