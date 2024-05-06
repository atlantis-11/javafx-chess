package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Controller class for the game over window.
 */
public class GameOverWindowController {
    @FXML
    public Text resultText;
    @FXML
    public Text reasonText;
    private final String result;
    private final String reason;

    /**
     * Constructs a GameOverWindowController with the
     * given result (win or draw) and reason for the result.
     *
     * @param result The result (win or draw).
     * @param reason The reason for the result.
     */
    public GameOverWindowController(String result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    @FXML
    public void initialize() {
        resultText.setText(result);
        reasonText.setText(reason);
    }
}
