package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;

import java.util.function.Consumer;

/**
 * Controller class for the promotion window.
 */
public class PromotionWindowController {
    @FXML
    private HBox mainRow;
    private final Stage stage;
    private final PieceColor pieceColor;
    private final double squareSize;
    private final Consumer<PieceType> onSelected;

    /**
     * Constructs a PromotionWindowController.
     *
     * @param stage      The stage for the scene.
     * @param pieceColor The color of the piece being promoted.
     * @param squareSize The size of the square.
     * @param onSelected The callback to call once a piece type has been selected.
     */
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

    /** Gets image path for the specified piece type. */
    private String getPieceImagePath(PieceType pieceType) {
        String symbol = pieceType == PieceType.KNIGHT
            ? "n" : pieceType.name().substring(0, 1).toLowerCase();

        return (pieceColor == PieceColor.WHITE ? "w" : "b")
            + symbol + ".png";
    }
}
