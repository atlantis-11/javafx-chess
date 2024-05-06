package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;
import ua.edu.sumdu.chess.javafxchess.backend.moves.EnPassantMove;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pawn piece in the chess game.
 */
public class Pawn extends Piece {
    private final Direction dir;

    /** Constructs a pawn piece with the specified color. */
    public Pawn(PieceColor color) {
        super(color, PieceType.PAWN);
        dir = (color == PieceColor.WHITE) ? Direction.NORTH : Direction.SOUTH;
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        List<Move> moves = new ArrayList<>();

        boolean isPreLastRow = from.row() == (dir == Direction.NORTH ? 1 : 6);
        boolean isEnPassantRow = from.row() == (dir == Direction.NORTH ? 3 : 4);

        if (!isPreLastRow) {
            addOneSquareMoves(board, from, moves);
        } else {
            addPromotionMoves(board, from, moves);
        }

        if (!hasMoved) {
            addTwoSquaresMove(board, from, moves);
        }

        if (isEnPassantRow) {
            addEnPassantMove(board, from, moves);
        }

        return moves;
    }

    /**
     * Gets the possible positions for a one-square move.
     *
     * @param board The chessboard.
     * @param from The position of the pawn.
     * @return A list of possible positions for a one-square move.
     */
    private List<Position> getOneSquarePositions(Board board, Position from) {
        List<Position> positions = new ArrayList<>();

        Position oneForwardPos = from.add(dir);

        if (board.getPiece(oneForwardPos) == null) {
            positions.add(oneForwardPos);
        }

        for (Direction hDir : Direction.HORIZONTAL_DIRS) {
            Position diagPos = oneForwardPos.add(hDir);

            if (board.getPiece(diagPos) != null
                    && board.getPiece(diagPos).getColor() != color) {
                positions.add(diagPos);
            }
        }

        return positions;
    }

    /**
     * Adds regular one-square moves to the list of moves.
     *
     * @param board The chessboard.
     * @param from The position of the pawn.
     * @param moves The list of moves to which regular moves are added.
     */
    private void addOneSquareMoves(Board board, Position from, List<Move> moves) {
        moves.addAll(getOneSquarePositions(board, from).stream()
            .map(to -> new RegularMove(from, to))
            .toList());
    }

    /**
     * Adds promotion moves to the list of moves.
     *
     * @param board The chessboard.
     * @param from The position of the pawn.
     * @param moves The list of moves to which promotion moves are added.
     */
    private void addPromotionMoves(Board board, Position from, List<Move> moves) {
        moves.addAll(getOneSquarePositions(board, from).stream()
            .map(to -> new PromotionMove(from, to))
            .toList());
    }

    /**
     * Adds a two-squares move to the list of moves if available.
     *
     * @param board The chessboard.
     * @param from The position of the pawn.
     * @param moves The list of moves to which the two-squares move is added.
     */
    private void addTwoSquaresMove(Board board, Position from, List<Move> moves) {
        Position oneForwardPos = from.add(dir);
        Position twoForwardPos = oneForwardPos.add(dir);

        if (board.getPiece(oneForwardPos) == null
                && board.getPiece(twoForwardPos) == null) {
            moves.add(new RegularMove(from, twoForwardPos));
        }
    }

    /**
     * Adds an en passant move to the list of moves if available.
     *
     * @param board The chessboard.
     * @param from The position of the pawn.
     * @param moves The list of moves to which the en passant move is added.
     */
    private void addEnPassantMove(Board board, Position from, List<Move> moves) {
        Position oneForwardPos = from.add(dir);

        for (Direction hDir : Direction.HORIZONTAL_DIRS) {
            Position posToTheSide = from.add(hDir);
            Piece pieceToTheSide = board.getPiece(posToTheSide);

            if (pieceToTheSide != null
                    && pieceToTheSide.getType() == PieceType.PAWN
                    && pieceToTheSide.getColor() != color
                    && board.getLastMove().getTo().equals(posToTheSide)
                    && getMoveRowDelta(board.getLastMove()) == 2) {
                moves.add(new EnPassantMove(from, oneForwardPos.add(hDir)));
                return;
            }
        }
    }

    /** Gets the row delta of a move. */
    private int getMoveRowDelta(Move move) {
        return Math.abs(move.getTo().row() - move.getFrom().row());
    }

    @Override
    public Piece makeCopy() {
        Pawn p = new Pawn(color);
        p.hasMoved = hasMoved;
        return p;
    }
}