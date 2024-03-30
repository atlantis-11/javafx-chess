package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GameOverWindowController {
    @FXML
    public Text resultText;
    @FXML
    public Text reasonText;
    private final String result;
    private final String reason;

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
