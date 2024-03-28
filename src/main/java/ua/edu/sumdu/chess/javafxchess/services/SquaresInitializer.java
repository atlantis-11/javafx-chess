package ua.edu.sumdu.chess.javafxchess.services;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class SquaresInitializer {
    public static StackPane[][] initializeSquares(GridPane boardGridPane) {
        StackPane[][] squares = new StackPane[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = createSquare(row, col);
                boardGridPane.add(square, col, row);
                squares[row][col] = square;
            }
        }

        return squares;
    }

    private static StackPane createSquare(int row, int col) {
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle();

        if ((row + col) % 2 == 0) {
            rectangle.getStyleClass().add("whiteSquare");
        } else {
            rectangle.getStyleClass().add("blackSquare");
        }

        square.getChildren().add(rectangle);
        return square;
    }
}