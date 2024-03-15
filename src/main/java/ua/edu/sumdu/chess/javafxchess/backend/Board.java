package ua.edu.sumdu.chess.javafxchess.backend;

import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public Piece getPiece(Position pos) {
        if (isOnBoard(pos)) {
            return board[pos.row()][pos.col()];
        }
        return null;
    }

    public Piece getPiece(int row, int col) {
        return getPiece(new Position(row, col));
    }

    public void setPiece(Position pos, Piece piece) {
        if (isOnBoard(pos)) {
            board[pos.row()][pos.col()] = piece;
        }
    }

    public void setPiece(int row, int col, Piece piece) {
        setPiece(new Position(row, col), piece);
    }

    public boolean isOnBoard(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 &&
               pos.col() >= 0 && pos.col() < 8;
    }

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

    private List<Position> getPositions(PieceColor color) {
        return getPositions().stream()
            .filter(pos -> getPiece(pos).getColor() == color)
            .toList();
    }

    public List<Move> getMoves(Position pos) {
        Piece piece = getPiece(pos);

        if (piece == null) {
            return new ArrayList<>();
        }

        return piece.getMoves(this, pos);
    }

    public List<Move> getLegalMoves(Position pos) {
        return getMoves(pos).stream()
            .filter(move -> move.isLegal(this))
            .toList();
    }

    public List<Move> getLegalMoves(PieceColor color) {
        return getPositions(color).stream()
            .map(this::getLegalMoves)
            .flatMap(Collection::stream)
            .toList();
    }

    public boolean isInCheck(PieceColor color) {
        PieceColor opponentColor = color == PieceColor.WHITE
            ? PieceColor.BLACK
            : PieceColor.WHITE;

        return getPositions(opponentColor).stream()
            .anyMatch(pos -> getPiece(pos).canCaptureKing(this, pos));
    }

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

    public String getFEN() {
        return new FENGenerator(this).getFEN();
    }

    public String getFENWithoutCounters() {
        String FEN = getFEN();
        int secondLastSpaceIndex = FEN.lastIndexOf(' ', FEN.lastIndexOf(' ') - 1);
        return FEN.substring(0, secondLastSpaceIndex);
    }

    public Board makeCopy() {
        Board board = new Board();
        for (Position pos : getPositions()) {
            board.setPiece(pos, getPiece(pos).makeCopy());
        }
        return board;
    }
}
