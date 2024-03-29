package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import ua.edu.sumdu.chess.javafxchess.backend.Game;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;
import ua.edu.sumdu.chess.javafxchess.services.BoardDrawer;
import ua.edu.sumdu.chess.javafxchess.services.Resizer;
import ua.edu.sumdu.chess.javafxchess.services.SquaresInitializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private GridPane boardGridPane;
    @FXML
    private VBox mainColumn;
    @FXML
    private HBox topButtonRow;
    @FXML
    private HBox bottomButtonRow;
    @FXML
    private Button timerButtonBlack;
    @FXML
    private Button timerButtonWhite;
    private StackPane[][] squares;
    private final int timeInSeconds;
    @Setter
    private Game game;
    private Position selectedPos;
    private List<Move> currentLegalMoves = new ArrayList<>();

    private BoardDrawer boardDrawer;
    private Resizer resizer;

    public MainWindowController(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    @FXML
    public void initialize() {
        squares = SquaresInitializer.initializeSquares(boardGridPane);

        resizer = new Resizer(mainPane, boardGridPane, mainColumn,
            topButtonRow, bottomButtonRow);
        resizer.addMainPaneSizeChangeListeners(squares);

        timerButtonBlack.setText(convertSecondsToHMS(timeInSeconds));
        timerButtonWhite.setText(convertSecondsToHMS(timeInSeconds));

        boardDrawer = new BoardDrawer(squares);
    }

    public void setupGameEventsHandlers() {
        game.onMoveMade(e -> drawBoard());
    }
    public void setupTimerEventsHandlers() {
        game.onTimeUpdated(e -> updateTimer());
    }

    public void drawBoard() {
        boardDrawer.drawBoard(game.getBoard());
        resizer.updateSquaresSize(squares);
    }

    public void updateTimer(){
        if (game.getCurrentPlayer().getPieceColor().equals(PieceColor.WHITE)) {
            timerButtonWhite.setText(convertSecondsToHMS(game.getPlayerW().getTimeLeft()));
        } else {
            timerButtonBlack.setText(convertSecondsToHMS(game.getPlayerB().getTimeLeft()));
        }
    }

    public static String convertSecondsToHMS(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    @FXML
    public void handleSquareClick(MouseEvent mouseEvent) throws IOException {
        Position toPos = getPositionFromMouseEvent(mouseEvent);

        if (selectedPos == null) {
            handleSelection(toPos);
        } else {
            Move chosenMove = currentLegalMoves.stream()
                .filter(move -> move.getTo().equals(toPos))
                .findFirst()
                .orElse(null);

            if (chosenMove != null) {
                if (chosenMove instanceof PromotionMove) {
                    showPromotionWindow(toPos, mouseEvent);
                } else {
                    handleMove(toPos);
                }
            } else {
                Piece selectedPiece = game.getBoard().getPiece(selectedPos);

                clearSelection();

                Piece pieceAtPos = game.getBoard().getPiece(toPos);

                if (pieceAtPos != null
                        && pieceAtPos.getColor() == selectedPiece.getColor()) {
                    // select another piece
                    handleSelection(toPos);
                }
            }
        }
    }

    private Position getPositionFromMouseEvent(MouseEvent event) {
        int row = (int) (event.getY() / (boardGridPane.getHeight() / 8));
        int col = (int) (event.getX() / (boardGridPane.getWidth() / 8));

        return new Position(row, col);
    }

    private void showPromotionWindow(Position toPos, MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("promotionWindow.fxml"));
        loader.setControllerFactory(c ->
            new PromotionWindowController(
                stage,
                game.getBoard().getPiece(selectedPos).getColor(),
                resizer.getSquareSize(),
                selectedPieceType -> handlePromotionMove(toPos, selectedPieceType)
            )
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Promote to");

        stage.setX(mouseEvent.getScreenX());
        stage.setY(mouseEvent.getScreenY());

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainColumn.getScene().getWindow());

        stage.showAndWait();
    }

    private void handleSelection(Position pos) {
        currentLegalMoves = new ArrayList<>(game.getLegalMoves(pos));

        if (!currentLegalMoves.isEmpty()) {
            selectedPos = pos;

            highlightSelectedPosition();
            highlightLegalMoves();
        }
    }

    private void highlightSelectedPosition() {
        boardDrawer.highlightPosition(selectedPos);
        resizer.updateSquareSize(squares[selectedPos.row()][selectedPos.col()]);
    }

    private void highlightLegalMoves() {
        boardDrawer.highlightLegalMoves(currentLegalMoves);
        resizer.updateSquaresSize(currentLegalMoves.stream()
            .map(lm -> squares[lm.getTo().row()][lm.getTo().col()])
            .toList());
    }

    private void handleMove(Position toPos) {
        game.makeMove(selectedPos, toPos);
        selectedPos = null;
        currentLegalMoves.clear();
    }

    private void handlePromotionMove(Position toPos, PieceType promotionPieceType) {
        game.makeMove(selectedPos, toPos, promotionPieceType);
        selectedPos = null;
        currentLegalMoves.clear();
    }

    private void clearSelection() {
        selectedPos = null;
        currentLegalMoves.clear();
        drawBoard();
    }
}
