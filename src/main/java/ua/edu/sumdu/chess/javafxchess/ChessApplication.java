package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChessApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.setProperty("prism.lcdtext", "false");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("startWindow.fxml"));
        loader.setControllerFactory(c -> new StartWindowController());
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 400);
        String css = Objects.requireNonNull(getClass()
            .getResource("/styles/startWindow.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Start Menu");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(480);
        primaryStage.setMinHeight(250);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



