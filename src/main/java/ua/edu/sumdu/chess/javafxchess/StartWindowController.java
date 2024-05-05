package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ua.edu.sumdu.chess.javafxchess.backend.EngineGame;
import ua.edu.sumdu.chess.javafxchess.backend.Game;
import ua.edu.sumdu.chess.javafxchess.backend.exceptions.InvalidInputException;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;
import ua.edu.sumdu.chess.javafxchess.services.IconManager;

import java.io.IOException;
import java.util.Objects;

public class StartWindowController {
    @FXML
    private ToggleGroup optionGroup;
    @FXML
    public TextField timeMinField;
    @FXML
    public TextField timeSecField;
    @FXML
    public TextField difficultyField;
    @FXML
    public ComboBox<String> pieceColorComboBox;

    @FXML
    public void handleStartClick() throws IOException {
        RadioButton rb = (RadioButton) optionGroup.getSelectedToggle();

        try {
            if (rb.getId().equals("twoPlayerOption")) {
                startTwoPlayerGame(getTimeInSeconds());
            } else {
                startEngineGame(getPieceColor(), getDifficulty());
            }
        } catch (InvalidInputException e) {
            showValidationError(e.getMessage());
        }
    }

    private int getTimeInSeconds() {
        try {
            int timeMin = parseInt(timeMinField.getText());
            int timeSec = parseInt(timeSecField.getText());

            if (timeMin >= 0 && timeSec >= 0) {
                int timeInSeconds = timeMin * 60 + timeSec;

                if (timeInSeconds >= 30 && timeInSeconds <= 10 * 60 * 60) {
                    return timeInSeconds;
                }

                throw new InvalidInputException("Time setting has to be in 30 seconds to 10 hours range");
            }

            throw new InvalidInputException("Minutes and seconds have to be non negative values");
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid time value");
        }
    }

    private int getDifficulty() {
        try {
            int difficulty = parseInt(difficultyField.getText());

            if (difficulty >= 0 && difficulty <= 20) {
                return difficulty;
            }

            throw new InvalidInputException("Difficulty has to be 0 to 20");
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid difficulty value");
        }
    }

    private PieceColor getPieceColor() {
        return PieceColor.valueOf(
            pieceColorComboBox.getSelectionModel().getSelectedItem()
            .toUpperCase()
        );
    }

    private int parseInt(String str) {
        str = str.trim();
        return !str.isEmpty() ? Integer.parseInt(str) : 0;
    }

    private void startTwoPlayerGame(int timeInSeconds) throws IOException {
        Game game = new Game(timeInSeconds);
        showMainWindow(game);
    }

    private void startEngineGame(PieceColor pieceColor, int difficulty) throws IOException {
        Game game = new EngineGame(pieceColor, difficulty);
        showMainWindow(game);
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Validation error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMainWindow(Game game) throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        loader.setControllerFactory(c -> new MainWindowController(game));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        String css = Objects.requireNonNull(getClass()
            .getResource("/styles/mainWindow.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Chess");
        IconManager.addIcon(stage);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(450);
        stage.show();

        // close current window
        ((Stage) timeMinField.getScene().getWindow()).close();
    }
}
