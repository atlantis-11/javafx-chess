package ua.edu.sumdu.chess.javafxchess.backend;

import lombok.Getter;

@Getter
public class Direction {
    private final int rowDelta;
    private final int colDelta;

    public Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public static final Direction North = new Direction(-1, 0);
    public static final Direction NorthEast = new Direction(-1, 1);
    public static final Direction East = new Direction(0, 1);
    public static final Direction SouthEast = new Direction(1, 1);
    public static final Direction South = new Direction(1, 0);
    public static final Direction SouthWest = new Direction(1, -1);
    public static final Direction West = new Direction(0, -1);
    public static final Direction NorthWest = new Direction(-1, -1);
    public static final Direction[] HorizontalDirs = { East, West };
    public static final Direction[] VerticalDirs = { North, South };
}
