package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;

import java.util.List;

public class Queen extends Piece {
    private static final Direction[] dirs = {
        Direction.North,
        Direction.NorthEast,
        Direction.East,
        Direction.SouthEast,
        Direction.South,
        Direction.SouthWest,
        Direction.West,
        Direction.NorthWest
    };

    public Queen(PieceColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Move> getMoves(Board board, Position from) {
        return getMovesInDirs(board, from, dirs);
    }

    @Override
    public Piece makeCopy() {
        Queen q = new Queen(color);
        q.hasMoved = hasMoved;
        return q;
    }
}
