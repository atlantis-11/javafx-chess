package ua.edu.sumdu.chess.javafxchess.backend;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;

/**
 * Generates FEN strings for the current state of a chess board.
 */
public class FENGenerator {
    private final Board board;

    /**
     * Constructs a FENGenerator for the specified chess board.
     *
     * @param board The chess board.
     */
    public FENGenerator(@NonNull Board board) {
        this.board = board;
    }

    /**
     * Generates the FEN string for the current state of the board.
     *
     * @return The FEN string.
     */
    public String getFEN() {
        return getPiecePlacement() + ' '
            + getSideToMove() + ' '
            + getCastlingAbility() + ' '
            + getEnPassantTargetSquare() + ' '
            + board.getHalfmoveClock() + ' '
            + board.getFullmoveCounter();
    }

    /** Generates the piece placement part of the FEN string. */
    private String getPiecePlacement() {
        StringBuilder fen = new StringBuilder();
        int emptySquares = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String symbol = getPieceSymbol(row, col);

                if (symbol != null) {
                    if (emptySquares != 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    fen.append(symbol);
                } else {
                    emptySquares++;

                    if (col == 7) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                }
            }

            if (row != 7) {
                fen.append('/');
            }
        }

        return fen.toString();
    }

    /** Gets the symbol representing a piece at a specific position. */
    private String getPieceSymbol(int row, int col) {
        Piece piece = board.getPiece(row, col);

        if (piece == null) {
            return null;
        }

        String symbol = piece.getType() == PieceType.KNIGHT
            ? "n" : piece.getType().name().substring(0, 1).toLowerCase();

        return piece.getColor() == PieceColor.WHITE
            ? symbol.toUpperCase()
            : symbol;
    }

    /** Determines the side to move in the FEN string. */
    private char getSideToMove() {
        if (board.getLastMove() != null
            && board.getPiece(board.getLastMove().getTo())
                .getColor() == PieceColor.WHITE) {
            return 'b';
        } else {
            return 'w';
        }
    }

    /** Gets the castling ability part of the FEN string. */
    private String getCastlingAbility() {
        String castlingAbility = getCastlingAbility(PieceColor.WHITE)
            + getCastlingAbility(PieceColor.BLACK);

        return castlingAbility.isEmpty()
            ? "-"
            : castlingAbility;
    }

    /** Gets the castling ability for a specific color. */
    private String getCastlingAbility(PieceColor color) {
        StringBuilder castlingAbility = new StringBuilder();
        boolean isWhite = color == PieceColor.WHITE;
        int row = isWhite ? 7 : 0;

        if (pieceHasNotMoved(row, 4)) {
            if (pieceHasNotMoved(row, 0)) {
                castlingAbility.append('q');
            }
            if (pieceHasNotMoved(row, 7)) {
                castlingAbility.append('k');
            }
        }

        return isWhite
            ? castlingAbility.toString().toUpperCase()
            : castlingAbility.toString();
    }

    /** Checks if a piece has moved from its starting position. */
    private boolean pieceHasNotMoved(int row, int col) {
        Piece piece = board.getPiece(row, col);
        return piece != null && !piece.isHasMoved();
    }

    /** Gets the en passant target square part of the FEN string. */
    private String getEnPassantTargetSquare() {
        Move lastMove = board.getLastMove();

        if (lastMove != null &&
            board.getPiece(lastMove.getTo()).getType() == PieceType.PAWN &&
            Math.abs(lastMove.getTo().row() - lastMove.getFrom().row()) == 2) {

            char file = (char) ('a' + lastMove.getTo().col());
            String rank = String.valueOf(
                lastMove.getTo().row() - lastMove.getFrom().row() > 0
                    ? 7 - lastMove.getFrom().row()
                    : 9 - lastMove.getFrom().row()
            );

            return file + rank;
        }

        return "-";
    }
}
