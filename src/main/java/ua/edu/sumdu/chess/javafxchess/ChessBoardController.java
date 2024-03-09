package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Platform;
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

import java.util.List;

public class ChessBoardController {
    @FXML
    private GridPane boardGridPane;
    @FXML
    private AnchorPane boardPane;
    private StackPane[][] squareStackPanes;
    private Position selectedPos;

    @Getter
    @Setter
    private Game game;
    private final Color blackSquare = Color.rgb(119, 153, 84);
    private final Color whiteSquare = Color.rgb(233, 237, 204);
    private final Color highlightedSquareColor = Color.rgb(255, 255, 51, 0.5);

    @FXML
    void initialize() {
        initializeSquareStackPanes();
        addBoardListeners();
        Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(),
                boardPane.getHeight()));
    }

    private void initializeSquareStackPanes() {
        squareStackPanes = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane squareStackPane = createSquareStackPane(row, col);
                boardGridPane.add(squareStackPane, col, row);
                squareStackPanes[row][col] = squareStackPane;
            }
        }
    }

    private StackPane createSquareStackPane(int row, int col) {
        StackPane squareStackPane = new StackPane();
        Rectangle rectangle = new Rectangle();
        rectangle.setFill((row + col) % 2 == 0 ? whiteSquare : blackSquare);
        squareStackPane.getChildren().add(rectangle);
        return squareStackPane;
    }

    private void addBoardListeners() {
        boardPane.widthProperty().addListener((observableValue,
                                               oldWidth, newWidth) -> {
            double height = boardPane.getHeight();
            if (newWidth.doubleValue() / height > 1) {
                adjustSquareSize(newWidth.doubleValue(), height);
            }
        });

        boardPane.heightProperty().addListener((observableValue,
                                                oldHeight, newHeight) -> {
            double width = boardPane.getWidth();
            if (newHeight.doubleValue() / width > 1) {
                adjustSquareSize(width, newHeight.doubleValue());
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
                StackPane squareStackPane = squareStackPanes[i][j];
                Position position = new Position(i, j);
                Piece piece = board.getPiece(position);

                if (squareStackPane.getChildren().size() > 1) {
                    squareStackPane.getChildren().
                            remove(1, squareStackPane.getChildren().size());
                }

                highlightLastMove(squareStackPane, position, from, to);
                drawPiece(squareStackPane, piece);
            }
        }
    }

    private void highlightLastMove(StackPane squareStackPane,
                                   Position position, Position from,
                                   Position to) {
        if ((position.equals(from)) || (position.equals(to))) {
            Rectangle rectangleLastMove = new Rectangle();
            rectangleLastMove.setFill(highlightedSquareColor);

            squareStackPane.getChildren().add(rectangleLastMove);
        }
    }

    private void drawPiece(StackPane squareStackPane, Piece piece) {
        if (piece != null) {
            ImageView pieceView = new ImageView();
            pieceView.setFitWidth(1);
            pieceView.setFitHeight(1);
            squareStackPane.getChildren().add(pieceView);

            Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(),
                    boardPane.getHeight()));

            pieceView.setImage(new Image(getImagePath(piece)));
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
                addLegalMoves(move);
            }

            Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(),
                    boardPane.getHeight()));
        }
    }

    private void addLegalMoves(Move move) {
        int rowLM = move.getTo().row();
        int colLM = move.getTo().col();
        Circle circle = createLegalMoves(move);

        squareStackPanes[rowLM][colLM].getChildren().add(circle);
    }

    private Circle createLegalMoves(Move move) {
        Circle circle = new Circle();
        int rowLegalMove = move.getTo().row();
        int colLegalMove = move.getTo().col();
        StackPane stackPane = squareStackPanes[rowLegalMove][colLegalMove];

        if (((stackPane.getChildren().size() > 1) &&
                !(stackPane.getChildren().get(1) instanceof Rectangle) ||
                stackPane.getChildren().size() > 2)) {
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

    private void adjustSquareSize(double width, double height) {
        double squareSize = 0.8 * Math.min(width, height) / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane squareStackPane = squareStackPanes[row][col];
                squareStackPane.setPrefSize(squareSize, squareSize);

                adjustChildNodeSizes(squareStackPane, squareSize);
            }
        }
    }

    private void adjustChildNodeSizes(StackPane squareStackPane,
                                      double squareSize) {
        int squareStackSize = squareStackPane.getChildren().size();

        for (int i = 0; i < squareStackSize; i++) {
            if (squareStackPane.getChildren().get(i)
                    instanceof Rectangle rectangleLM) {
                rectangleLM.setWidth(squareSize);
                rectangleLM.setHeight(squareSize);
            } else if (squareStackPane.getChildren().get(i)
                    instanceof Circle circleLP) {
                circleLP.setRadius(squareSize / 4);
            } else if (squareStackPane.getChildren().get(i)
                    instanceof ImageView imgPiece) {
                imgPiece.setFitWidth(squareSize);
                imgPiece.setFitHeight(squareSize);
            }
        }
    }
}
