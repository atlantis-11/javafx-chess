package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

public class Bishop extends Piece {
    private static final Direction[] dirs = {
        Direction.NorthEast,
        Direction.SouthEast,
        Direction.SouthWest,
        Direction.NorthWest
    };

    public Bishop(PieceColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Move> getMoves(Board board, Position from) {
        return getMovesInDirs(board, from, dirs);
    }

    @Override
    public Piece makeCopy() {
        Bishop b = new Bishop(color);
        b.hasMoved = hasMoved;
        return b;
    }
}
