package ua.edu.sumdu.chess.javafxchess.backend.moves;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;

/**
 * Represents an en passant move in the chess game.
 */
public class EnPassantMove extends Move {
    /** Constructs an en passant move with the
     * specified 'from' and 'to' positions. */
    public EnPassantMove(Position from, Position to) {
        super(from, to);
    }

    @Override
    protected void doExecute(Board board) {
        Piece movedPiece = board.getPiece(from);
        board.setPiece(to, movedPiece);
        board.setPiece(from, null);
        movedPiece.setHasMoved(true);

        Position capturedPos = new Position(from.row(), to.col());
        board.setPiece(capturedPos, null);
    }
}
