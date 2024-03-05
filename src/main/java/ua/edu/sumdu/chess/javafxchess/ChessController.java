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

public class ChessController {
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane pane;
    private StackPane[][] cellStackPanes;
    private Position selectedPos;

    @Getter
    @Setter
    private Game game;

    @FXML
    void click(MouseEvent event) {
        int row = (int) (event.getY() / (gridPane.getHeight() / 8));
        int col = (int) (event.getX() / (gridPane.getWidth() / 8));
        Position pos = new Position(row, col);


        if (selectedPos == null) {
            List<Move> legalMoves = game.getLegalMoves(pos);

            if (!legalMoves.isEmpty()) {
                selectedPos = pos;

                for (Move move : legalMoves) {
                    int rowLM = move.getTo().row();
                    int colLM = move.getTo().col();
                    Circle circle = new Circle();
                    StackPane stackPane = cellStackPanes[rowLM][colLM];

                    if (((stackPane.getChildren().size() > 1) && !(stackPane.getChildren().get(1) instanceof Rectangle)|| stackPane.getChildren().size() > 2)){
                        circle.getStyleClass().add("LM");
                    } else {
                        circle.getStyleClass().add("LM-2");
                    }

                    cellStackPanes[rowLM][colLM].getChildren().add(circle);
                }

                Platform.runLater(() -> adjustSquareSize(pane.getWidth(), pane.getHeight()));
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
        Color blackCell = Color.rgb(119, 153, 84);
        Color whiteCell = Color.rgb(233, 237, 204);

        cellStackPanes = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cellStackPane = new StackPane();
                cellStackPanes[row][col] = cellStackPane;
                gridPane.add(cellStackPane, col, row);

                Rectangle rectangle = new Rectangle();
                rectangle.setFill((row + col) % 2 == 0 ? whiteCell : blackCell);

                cellStackPane.getChildren().add(rectangle);
            }
        }

        pane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            double height = pane.getHeight();
            if (newWidth.doubleValue() / height > 1) {
                adjustSquareSize(newWidth.doubleValue(), height);
            }
        });

        pane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            double width = pane.getWidth();
            if (newHeight.doubleValue() / width > 1) {
                adjustSquareSize(width, newHeight.doubleValue());
            }
        });

        Platform.runLater(() -> adjustSquareSize(pane.getWidth(), pane.getHeight()));
    }

    public void drawBoard(Board board) {
        Color stepColor = Color.rgb(244, 244, 128, 0.5);
        Position from = null, to = null;
        if (board.getLastMove() != null) {
            from = board.getLastMove().getFrom();
            to = board.getLastMove().getTo();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane cellStackPane = cellStackPanes[i][j];
                Position position = new Position(i, j);
                Piece piece = board.getPiece(position);

                int cellSize = cellStackPane.getChildren().size();
                if (cellSize > 1) {
                    cellStackPane.getChildren().remove(1, cellStackPane.getChildren().size());
                }

                if ((position.equals(from)) || (position.equals(to))) {
                    StackPane cellStackPanelLM = cellStackPanes[i][j];
                    Rectangle rectangleLM = new Rectangle();
                    rectangleLM.setFill(stepColor);

                    cellStackPanelLM.getChildren().add(rectangleLM);
                }

                if (piece != null) {
                    ImageView pieceView = new ImageView();
                    cellStackPane.getChildren().add(pieceView);

                    Platform.runLater(() -> adjustSquareSize(pane.getWidth(), pane.getHeight()));

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
                StackPane cellStackPane = cellStackPanes[row][col];
                cellStackPane.setPrefSize(squareSize, squareSize);

                int size = cellStackPane.getChildren().size();

                for (int i = 0; i < size; i++) {
                    if (cellStackPane.getChildren().get(i) instanceof Rectangle rectangleLM) {
                        rectangleLM.setWidth(squareSize);
                        rectangleLM.setHeight(squareSize);
                    } else if (cellStackPane.getChildren().get(i) instanceof Circle circleLP) {
                        circleLP.setRadius(squareSize / 4);
                    } else if (cellStackPane.getChildren().get(i) instanceof ImageView imgPiece) {
                        imgPiece.setFitWidth(squareSize);
                        imgPiece.setFitHeight(squareSize);
                    }
                }
            }
        }
    }


}
