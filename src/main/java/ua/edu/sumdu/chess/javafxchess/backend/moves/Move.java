package ua.edu.sumdu.chess.javafxchess.backend.moves;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;
import lombok.Getter;

@Getter
public abstract class Move {
    protected final Position from;
    protected final Position to;

    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    private void preExecute(Board board) {
        board.setLastMove(this);

        if (board.getPiece(from).getColor() == PieceColor.BLACK) {
            board.setFullmoveCounter(board.getFullmoveCounter() + 1);
        }

        if (board.getPiece(from).getType() == PieceType.PAWN ||
            board.getPiece(to) != null) {

            board.setHalfmoveClock(0);
        } else {
            board.setHalfmoveClock(board.getHalfmoveClock() + 1);
        }
    }

    private void postExecute(Board board) {
        if (!(this instanceof RegularMove) ||
            board.getPiece(to).getType() == PieceType.PAWN) {

            board.getRepetitionFENHistory().clear();
        } else {
            board.getRepetitionFENHistory().add(board.getFENWithoutCounters());
        }
    }

    protected abstract void doExecute(Board board);

    public void execute(Board board) {
        preExecute(board);
        doExecute(board);
        postExecute(board);
    }

    public boolean isLegal(Board board) {
        PieceColor currentColor = board.getPiece(from).getColor();
        Board copiedBoard = board.makeCopy();
        execute(copiedBoard);
        return !copiedBoard.isInCheck(currentColor);
    }
}
