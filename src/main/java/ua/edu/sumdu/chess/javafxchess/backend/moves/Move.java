package ua.edu.sumdu.chess.javafxchess.backend.moves;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a move in the chess game.
 */
@Getter
public abstract class Move {
    protected final Position from;
    protected final Position to;

    /**
     * Constructs a move with the specified 'from' and 'to' positions.
     *
     * @param from The starting position of the move.
     * @param to The destination position of the move.
     */
    public Move(@NonNull Position from, @NonNull Position to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Performs pre-execution tasks before executing the move.
     *
     * @param board The chessboard.
     */
    private void preExecute(Board board) {
        board.setLastMove(this);

        if (board.getPiece(from).getColor() == PieceColor.BLACK) {
            board.setFullmoveCounter(board.getFullmoveCounter() + 1);
        }

        if (board.getPiece(from).getType() == PieceType.PAWN
                || board.getPiece(to) != null) {
            board.setHalfmoveClock(0);
        } else {
            board.setHalfmoveClock(board.getHalfmoveClock() + 1);
        }
    }

    /**
     * Performs post-execution tasks after executing the move.
     *
     * @param board The chessboard.
     */
    private void postExecute(Board board) {
        if (!(this instanceof RegularMove)
                || board.getPiece(to).getType() == PieceType.PAWN) {
            board.getRepetitionFENHistory().clear();
        } else {
            board.getRepetitionFENHistory().add(board.getFENWithoutCounters());
        }
    }

    /**
     * Executes the move on the chessboard.
     *
     * @param board The chessboard.
     */
    protected abstract void doExecute(Board board);

    /**
     * Executes the move on the chessboard
     * with all dependent tasks.
     *
     * @param board The chessboard.
     */
    public void execute(@NonNull Board board) {
        preExecute(board);
        doExecute(board);
        postExecute(board);
    }

    /**
     * Checks if the move is legal on the given chessboard.
     *
     * @param board The chessboard.
     * @return True if the move is legal, otherwise false.
     */
    public boolean isLegal(@NonNull Board board) {
        PieceColor currentColor = board.getPiece(from).getColor();
        Board copiedBoard = board.makeCopy();
        execute(copiedBoard);
        return !copiedBoard.isInCheck(currentColor);
    }
}
