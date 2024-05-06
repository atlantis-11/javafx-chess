package ua.edu.sumdu.chess.javafxchess.backend;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a chess board.
 */
public class Board {
    private final Piece[][] board = new Piece[8][8];
    @Getter @Setter
    private Move lastMove;
    @Getter @Setter
    private int halfmoveClock;
    @Getter @Setter
    private int fullmoveCounter = 1;
    @Getter
    private final List<String> repetitionFENHistory = new ArrayList<>();

    /**
     * Gets the piece at the specified position.
     *
     * @param pos The position to check.
     * @return The piece at the specified position, or null if no piece is present.
     */
    public Piece getPiece(@NonNull Position pos) {
        if (isOnBoard(pos)) {
            return board[pos.row()][pos.col()];
        }

        return null;
    }

    /**
     * Gets the piece at the specified row and column.
     *
     * @param row The row of the position.
     * @param col The column of the position.
     * @return The piece at the specified row and column, or null if no piece is present.
     */
    public Piece getPiece(int row, int col) {
        return getPiece(new Position(row, col));
    }

    /**
     * Sets the piece at the specified position.
     *
     * @param pos The position to set the piece.
     * @param piece The piece to set.
     */
    public void setPiece(@NonNull Position pos, Piece piece) {
        if (isOnBoard(pos)) {
            board[pos.row()][pos.col()] = piece;
        }
    }

    /**
     * Sets the piece at the specified row and column.
     *
     * @param row The row of the position.
     * @param col The column of the position.
     * @param piece The piece to set.
     */
    public void setPiece(int row, int col, Piece piece) {
        setPiece(new Position(row, col), piece);
    }

    /**
     * Checks if the position is on the board.
     *
     * @param pos The position to check.
     * @return True if the position is on the board, otherwise false.
     */
    public boolean isOnBoard(@NonNull Position pos) {
        return pos.row() >= 0 && pos.row() < 8 &&
               pos.col() >= 0 && pos.col() < 8;
    }

    /**
     * Gets a list of positions with pieces on the board.
     *
     * @return A list of positions with pieces on the board.
     */
    public List<Position> getPositions() {
        List<Position> positions = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                if (getPiece(pos) != null) {
                    positions.add(pos);
                }
            }
        }

        return positions;
    }

    /**
     * Gets a list of positions with pieces of the specified color.
     *
     * @param color The color of the pieces.
     * @return A list of positions with pieces of the specified color.
     */
    private List<Position> getPositions(PieceColor color) {
        return getPositions().stream()
            .filter(pos -> getPiece(pos).getColor() == color)
            .toList();
    }

    /**
     * Gets a list of all moves for the piece at the specified position.
     *
     * @param pos The position of the piece.
     * @return A list of all moves for the piece at the specified position.
     */
    public List<Move> getMoves(@NonNull Position pos) {
        Piece piece = getPiece(pos);

        if (piece == null) {
            return Collections.emptyList();
        }

        return piece.getMoves(this, pos);
    }

    /**
     * Gets a list of legal moves for the piece at the specified position.
     *
     * @param pos The position of the piece.
     * @return A list of legal moves for the piece at the specified position.
     */
    public List<Move> getLegalMoves(@NonNull Position pos) {
        return getMoves(pos).stream()
            .filter(move -> move.isLegal(this))
            .toList();
    }

    /**
     * Gets a list of legal moves for pieces of the specified color.
     *
     * @param color The color of the pieces.
     * @return A list of legal moves for pieces of the specified color.
     */
    public List<Move> getLegalMoves(@NonNull PieceColor color) {
        return getPositions(color).stream()
            .map(this::getLegalMoves)
            .flatMap(Collection::stream)
            .toList();
    }

    /**
     * Checks if the specified color is in check.
     *
     * @param color The color to check.
     * @return True if the specified color is in check, otherwise false.
     */
    public boolean isInCheck(@NonNull PieceColor color) {
        PieceColor opponentColor = color == PieceColor.WHITE
            ? PieceColor.BLACK
            : PieceColor.WHITE;

        return getPositions(opponentColor).stream()
            .anyMatch(pos -> getPiece(pos).canCaptureKing(this, pos));
    }

    /**
     * Initializes the chess board with the standard piece arrangement.
     */
    public void initialize() {
        for (PieceColor color : PieceColor.values()) {
            int row;
            if (color == PieceColor.BLACK) {
                row = 0;
            } else {
                row = 7;
            }

            board[row][0] = new Rook(color);
            board[row][1] = new Knight(color);
            board[row][2] = new Bishop(color);
            board[row][3] = new Queen(color);
            board[row][4] = new King(color);
            board[row][5] = new Bishop(color);
            board[row][6] = new Knight(color);
            board[row][7] = new Rook(color);

            if (color == PieceColor.BLACK) {
                row = 1;
            } else {
                row = 6;
            }

            for (int i = 0; i < 8; i++) {
                board[row][i] = new Pawn(color);
            }
        }

        repetitionFENHistory.add(getFENWithoutCounters());
    }

    /** Gets the FEN representation of the board. */
    public String getFEN() {
        return new FENGenerator(this).getFEN();
    }

    /** Gets the FEN representation of the board without counters. */
    public String getFENWithoutCounters() {
        String FEN = getFEN();
        int secondLastSpaceIndex = FEN.lastIndexOf(' ', FEN.lastIndexOf(' ') - 1);
        return FEN.substring(0, secondLastSpaceIndex);
    }

    /** Creates a deep copy of the board. */
    public Board makeCopy() {
        Board board = new Board();
        for (Position pos : getPositions()) {
            board.setPiece(pos, getPiece(pos).makeCopy());
        }
        return board;
    }
}
