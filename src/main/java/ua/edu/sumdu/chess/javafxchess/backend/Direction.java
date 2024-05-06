package ua.edu.sumdu.chess.javafxchess.backend;

import lombok.Getter;

/**
 * Represents a direction on the chessboard.
 */
@Getter
public class Direction {
    public static final Direction NORTH = new Direction(-1, 0);
    public static final Direction NORTH_EAST = new Direction(-1, 1);
    public static final Direction EAST = new Direction(0, 1);
    public static final Direction SOUTH_EAST = new Direction(1, 1);
    public static final Direction SOUTH = new Direction(1, 0);
    public static final Direction SOUTH_WEST = new Direction(1, -1);
    public static final Direction WEST = new Direction(0, -1);
    public static final Direction NORTH_WEST = new Direction(-1, -1);
    public static final Direction[] HORIZONTAL_DIRS = { EAST, WEST };
    public static final Direction[] VERTICAL_DIRS = { NORTH, SOUTH };

    private final int rowDelta;
    private final int colDelta;

    /**
     * Constructs a Direction with the specified row and column deltas.
     *
     * @param rowDelta The change in row position.
     * @param colDelta The change in column position.
     */
    public Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }
}
