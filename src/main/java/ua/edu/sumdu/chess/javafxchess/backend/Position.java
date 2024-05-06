package ua.edu.sumdu.chess.javafxchess.backend;

/**
 * Represents a position on the chessboard.
 */
public record Position(int row, int col) {
    /**
     * Adds a direction to the current position and returns the new position.
     *
     * @param dir The direction to add.
     * @return The new position after adding the direction.
     */
    public Position add(Direction dir) {
        return new Position(
            this.row + dir.getRowDelta(),
            this.col + dir.getColDelta()
        );
    }
}
