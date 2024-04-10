package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

public class Bishop extends Piece {
    private static final Direction[] DIRS = {
        Direction.NORTH_EAST,
        Direction.SOUTH_EAST,
        Direction.SOUTH_WEST,
        Direction.NORTH_WEST
    };

    public Bishop(PieceColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        return getMovesInDirs(board, from, DIRS);
    }

    @Override
    public Piece makeCopy() {
        Bishop b = new Bishop(color);
        b.hasMoved = hasMoved;
        return b;
    }
}
