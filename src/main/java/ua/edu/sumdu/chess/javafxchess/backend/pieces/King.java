package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;
import ua.edu.sumdu.chess.javafxchess.backend.moves.CastlingMove;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a king piece in the chess game.
 */
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

    /** Constructs a king piece with the specified color. */
    public King(PieceColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        List<Move> moves = new ArrayList<>();

        addRegularMoves(board, from, moves);
        addCastlingMoves(board, from, moves);

        return moves;
    }

    /**
     * Adds regular moves (non-castling) for the king piece from the specified position.
     *
     * @param board The chessboard.
     * @param from The position of the king piece.
     * @param moves The list to which moves are added.
     */
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

    /**
     * Adds castling moves for the king piece from the specified position.
     *
     * @param board The chessboard.
     * @param from The position of the king piece.
     * @param moves The list to which moves are added.
     */
    private void addCastlingMoves(Board board, Position from, List<Move> moves) {
        if (hasMoved) return;

        for (Direction hDir : Direction.HORIZONTAL_DIRS) {
            if (canCastle(board, from, hDir)) {
                Position to = from.add(hDir).add(hDir);
                moves.add(new CastlingMove(from, to));
            }
        }
    }

    /**
     * Checks if the king piece can castle in the specified direction from the given position.
     *
     * @param board The chessboard.
     * @param from The position of the king piece.
     * @param dir The direction in which to check for castling.
     * @return True if the king can castle in the specified direction, otherwise false.
     */
    private boolean canCastle(Board board, Position from, Direction dir) {
        int rookCol = dir == Direction.EAST ? 7 : 0;
        Position rookPos = new Position(from.row(), rookCol);
        Piece rook = board.getPiece(rookPos);

        if (rook == null || rook.hasMoved) {
            return false;
        }

        for (Position pos = from.add(dir);
             !pos.equals(rookPos);
             pos = pos.add(dir)) {
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
