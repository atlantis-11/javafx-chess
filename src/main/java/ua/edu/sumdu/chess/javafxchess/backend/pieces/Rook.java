package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

public class Rook extends Piece {
    private static final Direction[] dirs = {
        Direction.North,
        Direction.East,
        Direction.South,
        Direction.West
    };

    public Rook(PieceColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Move> getMoves(Board board, Position from) {
        return getMovesInDirs(board, from, dirs);
    }

    @Override
    public Piece makeCopy() {
        Rook r = new Rook(color);
        r.hasMoved = hasMoved;
        return r;
    }
}
