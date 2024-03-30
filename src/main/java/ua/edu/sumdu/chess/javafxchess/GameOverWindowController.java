package ua.edu.sumdu.chess.javafxchess;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GameOverWindowController {
    private final String result;
    private final String reason;
    @FXML
    public Text resultText;
    @FXML
    public Text reasonText;

    public GameOverWindowController(String result, String reason) {
        this.result = result;
        this.reason = reason;
    }
    @FXML
    public void initialize() {
        reasonText.setText(reason);
        resultText.setText(result);
    }
}
