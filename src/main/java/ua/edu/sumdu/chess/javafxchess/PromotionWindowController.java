package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;

import java.util.function.Consumer;

public class PromotionWindowController {
    @FXML
    private HBox mainRow;
    private final Stage stage;
    private final PieceColor pieceColor;
    private final double squareSize;
    private final Consumer<PieceType> onSelected;

    public PromotionWindowController(Stage stage, PieceColor pieceColor,
                                     double squareSize, Consumer<PieceType> onSelected) {
        this.stage = stage;
        this.pieceColor = pieceColor;
        this.squareSize = squareSize;
        this.onSelected = onSelected;
    }

    @FXML
    public void initialize() {
        PieceType[] pieceTypes = new PieceType[] {
            PieceType.QUEEN,
            PieceType.KNIGHT,
            PieceType.ROOK,
            PieceType.BISHOP
        };

        mainRow.setPrefHeight(squareSize);
        mainRow.setPrefWidth(squareSize * 4);

        for (PieceType pieceType : pieceTypes) {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(getPieceImagePath(pieceType)));
            imageView.setFitHeight(squareSize);
            imageView.setFitWidth(squareSize);

            imageView.setOnMouseClicked(e -> {
                onSelected.accept(pieceType);
                stage.close();
            });

            mainRow.getChildren().add(imageView);
        }

        stage.setOnCloseRequest(e -> onSelected.accept(PieceType.QUEEN));
    }

    private String getPieceImagePath(PieceType pieceType) {
        String symbol = pieceType == PieceType.KNIGHT
            ? "n" : pieceType.name().substring(0, 1).toLowerCase();

        return (pieceColor == PieceColor.WHITE ? "w" : "b")
            + symbol + ".png";
    }
}
