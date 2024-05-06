package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import ua.edu.sumdu.chess.javafxchess.backend.EngineGame;
import ua.edu.sumdu.chess.javafxchess.backend.Game;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.events.MoveMadeEvent;
import ua.edu.sumdu.chess.javafxchess.backend.events.TimeUpdatedEvent;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceType;
import ua.edu.sumdu.chess.javafxchess.services.BoardDrawer;
import ua.edu.sumdu.chess.javafxchess.services.IconManager;
import ua.edu.sumdu.chess.javafxchess.services.Resizer;
import ua.edu.sumdu.chess.javafxchess.services.SquaresInitializer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller class for the main window of the chess application.
 */
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
    private Button timerWhite;
    @FXML
    private Button timerBlack;
    @FXML
    private Button drawButton;
    private StackPane[][] squares;
    private final Game game;
    private Position selectedPos;
    private List<Move> currentLegalMoves = new ArrayList<>();

    private BoardDrawer boardDrawer;
    private Resizer resizer;

    /**
     * Constructs a MainWindowController with the given game instance.
     *
     * @param game The game instance.
     */
    public MainWindowController(Game game) {
        this.game = game;
        setupGameEventsHandlers();
    }

    /**
     * Initializes the main window controller.
     */
    @FXML
    public void initialize() {
        squares = SquaresInitializer.initializeSquares(boardGridPane);

        resizer = new Resizer(mainPane, mainColumn, topButtonRow, bottomButtonRow);
        resizer.addMainPaneSizeChangeListeners(squares);

        boardDrawer = new BoardDrawer(squares);

        if (game.getTimeInSeconds() == 0) {
            timerWhite.setVisible(false);
            timerBlack.setVisible(false);
        } else {
            String time = secondsToTime(game.getTimeInSeconds());
            timerWhite.setText(time);
            timerBlack.setText(time);
            timerBlack.setDisable(true);
        }

        if (game instanceof EngineGame) {
            drawButton.setVisible(false);
        }

        Platform.runLater(() -> {
            game.start();
            drawBoard();
        });
    }

    /** Sets up event handlers for the game instance. */
    private void setupGameEventsHandlers() {
        game.onMoveMade(this::handleMoveMadeEvent);

        if (game.getTimeInSeconds() != 0) {
            game.onTimeUpdated(this::handleTimeUpdatedEvent);
        }

        game.onWin(e -> {
            try {
                String winner = StringUtils.capitalize(e.getWinner()
                    .toString().toLowerCase());
                String reason = e.getReason().toString().toLowerCase();
                showGameOverWindow(winner + " Won", "by " + reason);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });

        game.onDraw(e -> {
            try {
                String reason = e.getReason().toString()
                    .toLowerCase().replace('_', ' ');
                showGameOverWindow("Draw", "by " + reason);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });

        if (game instanceof EngineGame) {
            ((EngineGame) game).onStockfishErrorEvent(e ->
                showStockfishErrorAlert());
        }
    }

    /** Updates the 'disable' parameter of the clocks. */
    private void handleMoveMadeEvent(MoveMadeEvent e) {
        drawBoard();

        boolean isWhite = e.getCurrentColor().equals(PieceColor.WHITE);
        timerWhite.setDisable(isWhite);
        timerBlack.setDisable(!isWhite);
    }

    /** Draws or redraws the board. */
    private void drawBoard() {
        boardDrawer.drawBoard(game.getBoard());
        resizer.updateSquaresSize(squares);
    }

    /** Updates the clocks. */
    private void handleTimeUpdatedEvent(TimeUpdatedEvent e) {
        Button currentTimer = e.getCurrentColor().equals(PieceColor.WHITE)
            ? timerWhite
            : timerBlack;

        currentTimer.setText(secondsToTime(e.getTimeLeft()));
    }

    /**
     * Converts seconds to formatted time.
     *
     * @param seconds The time in seconds.
     * @return The formatted time string.
     */
    private String secondsToTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        String pattern = seconds >= 3600 ? "H:mm:ss" : "m:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return time.format(formatter);
    }

    /** Handles click on the square. */
    @FXML
    public void handleSquareClick(MouseEvent mouseEvent) throws IOException {
        Position toPos = getPositionFromMouseEvent(mouseEvent);

        if (selectedPos == null) {
            handleSelection(toPos);
        } else if (selectedPos.equals(toPos)) {
            clearSelection();
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

    /** Gets position on the board from the mouse event coordinates. */
    private Position getPositionFromMouseEvent(MouseEvent event) {
        int row = (int) (event.getY() / (boardGridPane.getHeight() / 8));
        int col = (int) (event.getX() / (boardGridPane.getWidth() / 8));

        return new Position(row, col);
    }

    /** Handles piece selection, shows legal moves, if any. */
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

    /** Sends move to the game instance. */
    private void handleMove(Position toPos) {
        game.makeMove(selectedPos, toPos);
        selectedPos = null;
        currentLegalMoves.clear();
    }

    /** Sends promotion move to the game instance. */
    private void handlePromotionMove(Position toPos, PieceType promotionPieceType) {
        game.makeMove(selectedPos, toPos, promotionPieceType);
        selectedPos = null;
        currentLegalMoves.clear();
    }

    /** Deselects the currently selected piece. */
    private void clearSelection() {
        selectedPos = null;
        currentLegalMoves.clear();
        drawBoard();
    }

    @FXML
    public void handleResignClick() {
        game.resign();
    }

    @FXML
    public void handleDrawClick() {
        game.drawByAgreement();
    }

    @FXML
    public void handleBackClick() throws IOException {
        game.stop();
        ChessApplication.getApplicationInstance().showStartWindow(null);
        ((Stage) mainColumn.getScene().getWindow()).close();
    }

    /** Shows window where player can choose a piece to promote to. */
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
        IconManager.addIcon(stage);
        stage.setResizable(false);

        stage.setX(mouseEvent.getScreenX());
        stage.setY(mouseEvent.getScreenY());

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainColumn.getScene().getWindow());

        stage.showAndWait();
    }

    private void showGameOverWindow(String result, String reason) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameOverWindow.fxml"));
        loader.setControllerFactory(c ->
            new GameOverWindowController(result, reason)
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass()
            .getResource("/styles/gameOverWindow.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("Game Over");
        IconManager.addIcon(stage);
        stage.setWidth(300);
        stage.setHeight(150);
        stage.setResizable(false);

        Window thisWindow = mainColumn.getScene().getWindow();

        stage.setX(thisWindow.getX() + (thisWindow.getWidth() - stage.getWidth()) / 2);
        stage.setY(thisWindow.getY() + (thisWindow.getHeight() - stage.getHeight()) / 2);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(thisWindow);

        stage.showAndWait();
    }

    private void showStockfishErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Stockfish error");
        alert.showAndWait();
    }
}
