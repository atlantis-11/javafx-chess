package ua.edu.sumdu.chess.javafxchess.services;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service class that assists in resizing of the elements
 * of the chessboard based on the window size changes.
 */
public class Resizer {
    private final BorderPane mainPane;
    private final VBox mainColumn;
    private final HBox topButtonRow;
    private final HBox bottomButtonRow;

    public Resizer(BorderPane mainPane, VBox mainColumn, HBox topButtonRow, HBox bottomButtonRow) {
        this.mainPane = mainPane;
        this.mainColumn = mainColumn;
        this.topButtonRow = topButtonRow;
        this.bottomButtonRow = bottomButtonRow;
    }

    /**
     * Adds listeners to the main pane size changes to handle resizing.
     *
     * @param squares The squares of the chessboard.
     */
    public void addMainPaneSizeChangeListeners(StackPane[][] squares) {
        mainPane.widthProperty().addListener(e -> handleMainPaneSizeChange(squares));
        mainPane.heightProperty().addListener(e -> handleMainPaneSizeChange(squares));
    }

    private void handleMainPaneSizeChange(StackPane[][] squares) {
        updateSquaresSize(squares);

        Platform.runLater(() -> {
            double boardSize = getSquareSize() * 8;

            mainColumn.setMaxWidth(boardSize);
            mainColumn.setMaxHeight(boardSize
                + topButtonRow.getHeight() + bottomButtonRow.getHeight());
        });
    }

    /**
     * Updates the size of the squares on the chessboard.
     *
     * @param squares The 2D array of squares of the chessboard.
     */
    public void updateSquaresSize(StackPane[][] squares) {
        updateSquaresSize(Stream.of(squares)
            .flatMap(Arrays::stream)
            .toList());
    }

    /**
     * Updates the size of the squares on the chessboard.
     *
     * @param squares The list of squares of the chessboard.
     */
    public void updateSquaresSize(List<StackPane> squares) {
        squares.forEach(this::updateSquareSize);
    }

    /** Updates the size of a single square on the chessboard. */
    public void updateSquareSize(StackPane square) {
        double squareSize = getSquareSize();

        square.setPrefSize(squareSize, squareSize);
        updateElementsSizeInSquare(square, squareSize);
    }

    /**
     * Calculates the size of a square on the chessboard
     * based on the main pane size.
     */
    public double getSquareSize() {
        double availableWidth = mainPane.getWidth()
            - mainPane.getPadding().getLeft()
            - mainPane.getPadding().getRight();

        double availableHeight = mainPane.getHeight()
            - mainPane.getPadding().getTop()
            - mainPane.getPadding().getBottom()
            - topButtonRow.getHeight()
            - bottomButtonRow.getHeight();

        return Math.min(availableWidth, availableHeight) / 8;
    }

    /** Updates size of the various nodes located on the square. */
    private void updateElementsSizeInSquare(StackPane square, double squareSize) {
        for (Node child : square.getChildren()) {
            if (child instanceof Rectangle rectangle) {
                rectangle.setWidth(squareSize);
                rectangle.setHeight(squareSize);
            } else if (child instanceof Circle circle) {
                boolean isWithCapture = circle.getStyleClass().stream()
                    .anyMatch(styleClass -> styleClass.equals("legalMoveWithCapture"));

                if (isWithCapture) {
                    double strokeWidth = squareSize * 0.07;
                    circle.setStrokeWidth(strokeWidth);
                    circle.setRadius((squareSize - strokeWidth) / 2);
                } else {
                    circle.setRadius(squareSize / 2);
                }
            } else if (child instanceof ImageView imageView) {
                imageView.setFitWidth(squareSize);
                imageView.setFitHeight(squareSize);
            }
        }
    }
}
