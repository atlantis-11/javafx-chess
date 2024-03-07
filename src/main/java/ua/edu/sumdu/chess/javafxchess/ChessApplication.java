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
    private static ChessBoardController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chessBoard.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("JavaFX Application");
        Scene scene = new Scene(root, 800, 600);
        String css = Objects.requireNonNull(getClass().getResource("/legalMoves.css")).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();

        Game game = new Game();
        game.onMoveMade(e -> controller.drawBoard());

        setupGameEventsHandlers(game);
        game.start();

        controller.setGame(game);
        controller.drawBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static void setupGameEventsHandlers(Game game) {
//        game.onTimeUpdated(e -> {
//            System.out.println(e.getCurrentColor().toString() + ": " + e.getTimeLeft());
//        });

//        game.onWin(e -> {
//            System.out.println("Winner: " + e.getWinner().toString()
//                    + ", reason: " + e.getReason().toString());
//            System.exit(0);
//        });
//
//        game.onDraw(e -> {
//            System.out.println("Draw" + ", reason: " + e.getReason().toString());
//            System.exit(0);
//        });
    }
}



