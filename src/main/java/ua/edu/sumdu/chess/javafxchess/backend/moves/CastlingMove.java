package ua.edu.sumdu.chess.javafxchess.backend.moves;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;

public class CastlingMove extends Move {
    public CastlingMove(Position from, Position to) {
        super(from, to);
    }

    @Override
    protected void doExecute(Board board) {
        Direction dir = getCastlingDir();
        int rookCol = dir == Direction.EAST ? 7 : 0;

        Piece king = board.getPiece(from);
        Piece rook = board.getPiece(from.row(), rookCol);

        Position newRookPos = from.add(dir);

        board.setPiece(to, king);
        board.setPiece(from, null);
        king.setHasMoved(true);

        board.setPiece(newRookPos, rook);
        board.setPiece(from.row(), rookCol, null);
        rook.setHasMoved(true);
    }

    @Override
    public boolean isLegal(@NonNull Board board) {
        PieceColor currentColor = board.getPiece(from).getColor();

        if (board.isInCheck(currentColor)) {
            return false;
        }

        Direction dir = getCastlingDir();
        Board copiedBoard = board.makeCopy();
        RegularMove oneSquareMove = new RegularMove(from, from.add(dir));
        oneSquareMove.execute(copiedBoard);

        // passed through check
        if (copiedBoard.isInCheck(currentColor)) {
            return false;
        }

        copiedBoard = board.makeCopy();
        execute(copiedBoard);
        return !copiedBoard.isInCheck(currentColor);
    }

    private Direction getCastlingDir() {
        return (to.col() - from.col()) > 0
            ? Direction.EAST
            : Direction.WEST;
    }
}
