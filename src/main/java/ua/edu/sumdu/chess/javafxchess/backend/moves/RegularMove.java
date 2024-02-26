package ua.edu.sumdu.chess.javafxchess.backend.moves;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;

public class RegularMove extends Move {
    public RegularMove(Position from, Position to) {
        super(from, to);
    }

    @Override
    protected void doExecute(Board board) {
        Piece movedPiece = board.getPiece(from);
        board.setPiece(to, movedPiece);
        board.setPiece(from, null);
        movedPiece.setHasMoved(true);
    }
}
