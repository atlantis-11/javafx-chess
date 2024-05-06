package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

/**
 * Represents a queen piece in the chess game.
 */
public class Queen extends Piece {
    private static final Direction[] DIRS = {
        Direction.NORTH,
        Direction.NORTH_EAST,
        Direction.EAST,
        Direction.SOUTH_EAST,
        Direction.SOUTH,
        Direction.SOUTH_WEST,
        Direction.WEST,
        Direction.NORTH_WEST
    };

    /** Constructs a queen piece with the specified color. */
    public Queen(PieceColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        return getMovesInDirs(board, from, DIRS);
    }

    @Override
    public Piece makeCopy() {
        Queen q = new Queen(color);
        q.hasMoved = hasMoved;
        return q;
    }
}
