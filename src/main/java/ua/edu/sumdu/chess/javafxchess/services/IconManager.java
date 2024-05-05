package ua.edu.sumdu.chess.javafxchess.services;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IconManager {
    public static void addIcon(Stage stage) {
        stage.getIcons().add(new Image("wn.png"));
    }
}
