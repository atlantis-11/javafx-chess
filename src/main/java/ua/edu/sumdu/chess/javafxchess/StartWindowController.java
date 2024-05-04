package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ua.edu.sumdu.chess.javafxchess.backend.exceptions.InvalidInputException;

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
    public void handleStartClick() {
        RadioButton rb = (RadioButton) optionGroup.getSelectedToggle();

        try {
            if (rb.getId().equals("twoPlayerOption")) {
                int timeInSeconds = getTimeInSeconds();
                startGame();
            } else {
                int difficulty = getDifficulty();
                startGame();
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

                if (timeInSeconds > 0 && timeInSeconds <= 10 * 60 * 60) {
                    return timeInSeconds;
                }

                throw new InvalidInputException("Time has to be greater than 0 seconds " +
                    "and less than 10 hours");
            }

            throw new InvalidInputException("Minutes and seconds have to be >= 0");
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

    private int parseInt(String str) {
        str = str.trim();
        return !str.isEmpty() ? Integer.parseInt(str) : 0;
    }

    private void startGame() {
        System.out.println("Starting game");
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Validation error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
