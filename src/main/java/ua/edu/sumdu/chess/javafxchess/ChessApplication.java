package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.edu.sumdu.chess.javafxchess.backend.Game;

import java.io.IOException;
import java.util.Objects;

public class ChessApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.setProperty("prism.lcdtext", "false");

        int timeInSeconds = 600;
        Game game = new Game(timeInSeconds);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        loader.setControllerFactory(c -> new MainWindowController(game));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        String css = Objects.requireNonNull(getClass()
            .getResource("/styles/mainWindow.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(450);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



