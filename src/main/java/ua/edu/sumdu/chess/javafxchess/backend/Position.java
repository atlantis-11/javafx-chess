package ua.edu.sumdu.chess.javafxchess.backend;

public record Position(int row, int col) {
    public Position add(Direction dir) {
        return new Position(
            this.row + dir.getRowDelta(),
            this.col + dir.getColDelta()
        );
    }
}
