package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

/**
 * Represents a rook piece in the chess game.
 */
public class Rook extends Piece {
    private static final Direction[] DIRS = {
        Direction.NORTH,
        Direction.EAST,
        Direction.SOUTH,
        Direction.WEST
    };

    /** Constructs a rook piece with the specified color. */
    public Rook(PieceColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        return getMovesInDirs(board, from, DIRS);
    }

    @Override
    public Piece makeCopy() {
        Rook r = new Rook(color);
        r.hasMoved = hasMoved;
        return r;
    }
}
