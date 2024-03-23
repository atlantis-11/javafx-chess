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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chessBoard.fxml"));
        Parent root = loader.load();
        ChessBoardController controller = loader.getController();

        Scene scene = new Scene(root, 800, 600);
        String css = Objects.requireNonNull(getClass().getResource("/style.css"))
            .toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.show();

        Game game = new Game();
        game.start();

        controller.setGame(game);
        controller.setupGameEventsHandlers();
        controller.drawBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



