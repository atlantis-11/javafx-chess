package ua.edu.sumdu.chess.javafxchess.backend;

import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;

public class FENGenerator {
    private final Board board;

    public FENGenerator(Board board) {
        this.board = board;
    }

    public String getFEN() {
        return getPiecePlacement() + ' ' +
            getSideToMove() + ' ' +
            getCastlingAbility() + ' ' +
            getEnPassantTargetSquare() + ' ' +
            board.getHalfmoveClock() + ' ' +
            board.getFullmoveCounter();
    }

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

    private String getPieceSymbol(int row, int col) {
        Piece piece = board.getPiece(row, col);

        if (piece == null) {
            return null;
        }

        String symbol = switch (piece.getType()) {
            case KNIGHT -> "n";
            default -> piece.getType().name().substring(0, 1).toLowerCase();
        };

        return piece.getColor() == PieceColor.WHITE ? symbol.toUpperCase() : symbol;
    }

    private char getSideToMove() {
        if (board.getLastMove() != null &&
            board.getPiece(board.getLastMove().getTo()).getColor() == PieceColor.WHITE) {
            
            return 'b';
        } else {
            return 'w';
        }
    }

    private String getCastlingAbility() {
        String castlingAbility = getCastlingAbility(PieceColor.WHITE)
            + getCastlingAbility(PieceColor.BLACK);

        return castlingAbility.isEmpty()
            ? "-"
            : castlingAbility;
    }

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

    private boolean pieceHasNotMoved(int row, int col) {
        Piece piece = board.getPiece(row, col);
        return piece != null && !piece.isHasMoved();
    }

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
