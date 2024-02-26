module ua.edu.sumdu.chess.javafxchess {
    requires javafx.controls;
    requires javafx.fxml;
    requires eventemitter;
    requires static lombok;

    opens ua.edu.sumdu.chess.javafxchess to javafx.fxml;
    exports ua.edu.sumdu.chess.javafxchess;
}