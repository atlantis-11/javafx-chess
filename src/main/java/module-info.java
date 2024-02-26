module ua.edu.sumdu.chess.javafxchess {
    requires javafx.controls;
    requires javafx.fxml;


    opens ua.edu.sumdu.chess.javafxchess to javafx.fxml;
    exports ua.edu.sumdu.chess.javafxchess;
}