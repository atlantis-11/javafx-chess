package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Game;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessBoardController {
    @FXML
    private GridPane boardGridPane;
    @FXML
    private AnchorPane boardPane;
    private StackPane[][] squares;
    private Position selectedPos;
    private List<StackPane> changedSquares;

    @Getter
    @Setter
    private Game game;
    private final Color blackSquare = Color.rgb(119, 153, 84);
    private final Color whiteSquare = Color.rgb(233, 237, 204);
    private final Color highlightedSquareColor = Color.rgb(255, 255, 51, 0.5);

    @FXML
    void initialize() {
        initializeSquares();
        addBoardListeners();
        changedSquares = new ArrayList<>();
    }

    private void initializeSquares() {
        squares = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = createSquare(row, col);
                boardGridPane.add(square, col, row);
                squares[row][col] = square;
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle();
        rectangle.setFill((row + col) % 2 == 0 ? whiteSquare : blackSquare);
        square.getChildren().add(rectangle);
        return square;
    }

    private void addBoardListeners() {
        boardPane.widthProperty().addListener((observableValue,
                                               oldWidth, newWidth) -> {
            double height = boardPane.getHeight();
            if (newWidth.doubleValue() / height > 1) {
                updateAllSquares(newWidth.doubleValue(), height);
            }
        });

        boardPane.heightProperty().addListener((observableValue,
                                                oldHeight, newHeight) -> {
            double width = boardPane.getWidth();
            if (newHeight.doubleValue() / width > 1) {
                updateAllSquares(width, newHeight.doubleValue());
            }
        });
    }

    public void drawBoard() {
        Position from = null, to = null;
        Board board = game.getBoard();
        if (board.getLastMove() != null) {
            from = board.getLastMove().getFrom();
            to = board.getLastMove().getTo();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = squares[i][j];
                Position position = new Position(i, j);
                Piece piece = board.getPiece(position);

                if (square.getChildren().size() > 1) {
                    square.getChildren().remove(1, square.getChildren().size());
                }

                highlightLastMove(square, position, from, to);
                drawPiece(square, piece);

                changedSquares.add(square);
            }
        }

        updateChangedSquares(getSquareSize(boardPane.getWidth(), boardPane.getHeight()));
    }

    private void highlightLastMove(StackPane square,
                                   Position position, Position from,
                                   Position to) {
        if ((position.equals(from)) || (position.equals(to))) {
            Rectangle rectangleLastMove = new Rectangle();
            rectangleLastMove.setFill(highlightedSquareColor);

            square.getChildren().add(rectangleLastMove);
        }
    }

    private void drawPiece(StackPane square, Piece piece) {
        if (piece != null) {
            ImageView pieceView = new ImageView();
            pieceView.setFitWidth(1);
            pieceView.setFitHeight(1);
            pieceView.setImage(new Image(getImagePath(piece)));

            square.getChildren().add(pieceView);
        }
    }

    private String getImagePath(Piece piece) {
        String symbol = switch (piece.getType()) {
            case KNIGHT -> "n";
            default -> piece.getType().name().substring(0, 1).toLowerCase();
        };

        return (piece.getColor() == PieceColor.WHITE ? "w" : "b")
                + symbol + ".png";
    }

    @FXML
    void moveClick(MouseEvent event) {
        Position pos = getPositionFromMouseEvent(event);

        if (selectedPos == null) {
            handleSelection(pos);
        } else {
            handleMove(pos);
        }
    }

    private Position getPositionFromMouseEvent(MouseEvent event) {
        int row = (int) (event.getY() / (boardGridPane.getHeight() / 8));
        int col = (int) (event.getX() / (boardGridPane.getWidth() / 8));
        return new Position(row, col);
    }

    private void handleSelection(Position pos) {
        List<Move> legalMoves = game.getLegalMoves(pos);

        if (!legalMoves.isEmpty()) {
            selectedPos = pos;

            for (Move move : legalMoves) {
                addLegalMove(move);
            }

            updateChangedSquares(getSquareSize(boardPane.getWidth(), boardPane.getHeight()));
        }
    }

    private void addLegalMove(Move move) {
        int rowLegalMove = move.getTo().row();
        int colLegalMove = move.getTo().col();
        StackPane square = squares[rowLegalMove][colLegalMove];
        Circle circle = createLegalMove(move);

        square.getChildren().add(circle);

        changedSquares.add(square);
    }

    private Circle createLegalMove(Move move) {
        Circle circle = new Circle();
        int rowLegalMove = move.getTo().row();
        int colLegalMove = move.getTo().col();
        StackPane square = squares[rowLegalMove][colLegalMove];

        if (((square.getChildren().size() > 1) &&
                !(square.getChildren().get(1) instanceof Rectangle) ||
                square.getChildren().size() > 2)) {
            circle.getStyleClass().add("legalMoveWithCapture");
        } else {
            circle.getStyleClass().add("legalMove");
        }

        return circle;
    }

    private void handleMove(Position pos) {
//        PieceType pieceType = tryParsePieceType("\n");
        game.makeMove(selectedPos, pos);
        selectedPos = null;
    }

//    private PieceType tryParsePieceType(String pieceTypeString) {
//        try {
//            return PieceType.valueOf(pieceTypeString.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            return PieceType.QUEEN;
//        }
//    }

    private void updateAllSquares(double width, double height) {
        double squareSize = getSquareSize(width, height);

        for (int row = 0; row < 8; row++) {
            changedSquares.addAll(Arrays.asList(squares[row]).subList(0, 8));
        }

        updateChangedSquares(squareSize);
    }

    private void updateChangedSquares(double squareSize){
        for (StackPane changedSquare : changedSquares) {
            changedSquare.setPrefSize(squareSize, squareSize);
            updateElementsSizeInSquare(changedSquare, squareSize);
        }

        changedSquares.clear();
    }

    private double getSquareSize(double width, double height){
        return 0.8 * Math.min(width, height) / 8;
    }

    private void updateElementsSizeInSquare(StackPane square, double squareSize){
        for (int i = 0; i < square.getChildren().size(); i++) {
            if (square.getChildren().get(i)
                    instanceof Rectangle rectangleLM) {
                rectangleLM.setWidth(squareSize);
                rectangleLM.setHeight(squareSize);
            } else if (square.getChildren().get(i)
                    instanceof Circle circleLP) {
                circleLP.setRadius(squareSize / 4);
            } else if (square.getChildren().get(i)
                    instanceof ImageView imgPiece) {
                imgPiece.setFitWidth(squareSize);
                imgPiece.setFitHeight(squareSize);
            }
        }
    }
}
