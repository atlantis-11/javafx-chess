package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import ua.edu.sumdu.chess.javafxchess.services.IconManager;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents the main application class for the chess game.
 */
public class ChessApplication extends Application {
    @Getter
    private static ChessApplication applicationInstance;

    /**
     * Initializes the application.
     * Sets the applicationInstance variable
     */
    @Override
    public void init() {
        applicationInstance = this;
    }

    /**
     * Starts the application by showing the start window.
     *
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.setProperty("prism.lcdtext", "false");

        showStartWindow(primaryStage);
    }

    /**
     * Shows the start menu for the application.
     *
     * @param stage The stage to load the start menu scene into, optional.
     */
    public void showStartWindow(Stage stage) throws IOException {
        if (stage == null) {
            stage = new Stage();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("startWindow.fxml"));
        loader.setControllerFactory(c -> new StartWindowController());
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 400);
        String css = Objects.requireNonNull(getClass()
            .getResource("/styles/startWindow.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Start Menu");
        IconManager.addIcon(stage);
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(250);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



