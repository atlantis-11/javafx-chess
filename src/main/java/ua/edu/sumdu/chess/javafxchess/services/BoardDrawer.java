package ua.edu.sumdu.chess.javafxchess.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;

import java.util.List;

public class BoardDrawer {
    private final StackPane[][] squares;

    public BoardDrawer(StackPane[][] squares) {
        this.squares = squares;
    }

    public void drawBoard(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = squares[i][j];
                Position position = new Position(i, j);
                Piece piece = board.getPiece(position);
                clearSquare(square);
                drawPiece(square, piece);
            }
        }

        highlightLastMoves(board);
    }

    private void clearSquare(StackPane square) {
        if (square.getChildren().size() > 1) {
            square.getChildren().remove(1, square.getChildren().size());
        }
    }

    private void highlightLastMoves(Board board) {
        if (board.getLastMove() != null) {
            highlightPosition(board.getLastMove().getFrom());
            highlightPosition(board.getLastMove().getTo());
        }
    }

    public void highlightPosition(Position pos) {
        StackPane square = squares[pos.row()][pos.col()];
        Rectangle highlight = new Rectangle();
        highlight.getStyleClass().add("highlightedSquare");

        square.getChildren().add(1, highlight);
    }

    private void drawPiece(StackPane square, Piece piece) {
        if (piece != null) {
            ImageView pieceImageView = new ImageView();
            pieceImageView.setFitWidth(1);
            pieceImageView.setFitHeight(1);
            pieceImageView.setImage(new Image(getPieceImagePath(piece)));

            square.getChildren().add(pieceImageView);
        }
    }

    private String getPieceImagePath(Piece piece) {
        String symbol = switch (piece.getType()) {
            case KNIGHT -> "n";
            default -> piece.getType().name().substring(0, 1).toLowerCase();
        };

        return (piece.getColor() == PieceColor.WHITE ? "w" : "b")
            + symbol + ".png";
    }

    public void highlightLegalMoves(List<Move> legalMoves) {
        for (Move move : legalMoves) {
            int legalMoveRow = move.getTo().row();
            int legalMoveCol = move.getTo().col();

            StackPane square = squares[legalMoveRow][legalMoveCol];
            Circle lastMoveCircle = createLegalMove(move);

            square.getChildren().add(lastMoveCircle);
        }
    }

    private Circle createLegalMove(Move move) {
        int legalMoveRow = move.getTo().row();
        int legalMoveCol = move.getTo().col();

        StackPane square = squares[legalMoveRow][legalMoveCol];
        Circle lastMoveCircle = new Circle();

        if ((square.getChildren().size() > 1
            && !(square.getChildren().get(1) instanceof Rectangle)
            || square.getChildren().size() > 2)) {
            lastMoveCircle.getStyleClass().add("legalMoveWithCapture");
        } else {
            lastMoveCircle.getStyleClass().add("legalMove");
        }

        return lastMoveCircle;
    }
}
