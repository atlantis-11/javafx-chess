package ua.edu.sumdu.chess.javafxchess.services;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/** Service class to add an icon to the stage. */
public class IconManager {
    /** Adds an app icon to the given stage. */
    public static void addIcon(Stage stage) {
        stage.getIcons().add(new Image("wn.png"));
    }
}
