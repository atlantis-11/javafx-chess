package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;
import ua.edu.sumdu.chess.javafxchess.backend.moves.CastlingMove;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
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

    public King(PieceColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Move> getMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        addRegularMoves(board, from, moves);
        addCastlingMoves(board, from, moves);

        return moves;
    }

    private void addRegularMoves(Board board, Position from, List<Move> moves) {
        for (Direction dir : DIRS) {
            Position to = from.add(dir);

            if (!board.isOnBoard(to)) {
                continue;
            }

            Piece toPiece = board.getPiece(to);
            if (toPiece != null && toPiece.getColor() == color) {
                continue;
            }

            moves.add(new RegularMove(from, to));
        }
    }

    private void addCastlingMoves(Board board, Position from, List<Move> moves) {
        if (hasMoved) return;

        for (Direction hDir : Direction.HORIZONTAL_DIRS) {
            if (canCastle(board, from, hDir)) {
                Position to = from.add(hDir).add(hDir);
                moves.add(new CastlingMove(from, to));
            }
        }
    }

    private boolean canCastle(Board board, Position from, Direction dir) {
        int rookCol = dir == Direction.EAST ? 7 : 0;
        Position rookPos = new Position(from.row(), rookCol);
        Piece rook = board.getPiece(rookPos);

        if (rook == null || rook.hasMoved) {
            return false;
        }

        for (Position pos = from.add(dir); !pos.equals(rookPos); pos = pos.add(dir)) {
            if (board.getPiece(pos) != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Piece makeCopy() {
        King k = new King(color);
        k.hasMoved = hasMoved;
        return k;
    }
}
