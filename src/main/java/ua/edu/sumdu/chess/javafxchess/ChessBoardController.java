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
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;

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
    private Color blackSquare = Color.rgb(119, 153, 84);
    private Color whiteSquare = Color.rgb(233, 237, 204);
    private Color stepColor = Color.rgb(244, 244, 128, 0.5);

    @FXML
    void moveClick(MouseEvent event) {
        int row = (int) (event.getY() / (boardGridPane.getHeight() / 8));
        int col = (int) (event.getX() / (boardGridPane.getWidth() / 8));
        Position pos = new Position(row, col);


        if (selectedPos == null) {
            List<Move> legalMoves = game.getLegalMoves(pos);

            if (!legalMoves.isEmpty()) {
                selectedPos = pos;

                for (Move move : legalMoves) {
                    int rowLM = move.getTo().row();
                    int colLM = move.getTo().col();
                    Circle circle = new Circle();
                    StackPane stackPane = squareStackPanes[rowLM][colLM];

                    if (((stackPane.getChildren().size() > 1) &&
                            !(stackPane.getChildren().get(1) instanceof Rectangle)||
                            stackPane.getChildren().size() > 2)){
                        circle.getStyleClass().add("legalMoveWithCapture");
                    } else {
                        circle.getStyleClass().add("legalMove");
                    }

                    squareStackPanes[rowLM][colLM].getChildren().add(circle);
                }

                Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(), boardPane.getHeight()));
            }
        } else {
            PieceType pieceType = tryParsePieceType("\n");

            game.makeMove(selectedPos, pos, pieceType);

            selectedPos = null;
        }

    }

    private PieceType tryParsePieceType(String pieceTypeString) {
        try {
            return PieceType.valueOf(pieceTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PieceType.QUEEN;
        }
    }

    @FXML
    void initialize() {
        squareStackPanes = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane squareStackPane = new StackPane();
                squareStackPanes[row][col] = squareStackPane;
                boardGridPane.add(squareStackPane, col, row);

                Rectangle rectangle = new Rectangle();
                rectangle.setFill((row + col) % 2 == 0 ? whiteSquare : blackSquare);

                squareStackPane.getChildren().add(rectangle);
            }
        }

        boardPane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            double height = boardPane.getHeight();
            if (newWidth.doubleValue() / height > 1) {
                adjustSquareSize(newWidth.doubleValue(), height);
            }
        });

        boardPane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            double width = boardPane.getWidth();
            if (newHeight.doubleValue() / width > 1) {
                adjustSquareSize(width, newHeight.doubleValue());
            }
        });

        Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(), boardPane.getHeight()));
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

                int squareSize = squareStackPane.getChildren().size();
                if (squareSize > 1) {
                    squareStackPane.getChildren().remove(1, squareStackPane.getChildren().size());
                }

                if ((position.equals(from)) || (position.equals(to))) {
                    StackPane squareStackPanelLM = squareStackPanes[i][j];
                    Rectangle rectangleLM = new Rectangle();
                    rectangleLM.setFill(stepColor);

                    squareStackPanelLM.getChildren().add(rectangleLM);
                }

                if (piece != null) {
                    ImageView pieceView = new ImageView();
                    squareStackPane.getChildren().add(pieceView);

                    Platform.runLater(() -> adjustSquareSize(boardPane.getWidth(), boardPane.getHeight()));

                    pieceView.setImage(new Image(getImagePath(piece)));

                }

            }
        }
    }

    private String getImagePath(Piece piece) {
        String symbol = switch (piece.getType()) {
            case KNIGHT -> "n";
            default -> piece.getType().name().substring(0, 1).toLowerCase();
        };

        return (piece.getColor() == PieceColor.WHITE ? "w" : "b") + symbol + ".png";
    }

    private void adjustSquareSize(double width, double height) {
        double squareSize = 0.8 * Math.min(width, height) / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane squareStackPane = squareStackPanes[row][col];
                squareStackPane.setPrefSize(squareSize, squareSize);

                int squareStackSize = squareStackPane.getChildren().size();

                for (int i = 0; i < squareStackSize; i++) {
                    if (squareStackPane.getChildren().get(i) instanceof Rectangle rectangleLM) {
                        rectangleLM.setWidth(squareSize);
                        rectangleLM.setHeight(squareSize);
                    } else if (squareStackPane.getChildren().get(i) instanceof Circle circleLP) {
                        circleLP.setRadius(squareSize / 4);
                    } else if (squareStackPane.getChildren().get(i) instanceof ImageView imgPiece) {
                        imgPiece.setFitWidth(squareSize);
                        imgPiece.setFitHeight(squareSize);
                    }
                }
            }
        }
    }


}
