package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a knight piece in the chess game.
 */
public class Knight extends Piece {
    /** Constructs a knight piece with the specified color. */
    public Knight(PieceColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> getMoves(@NonNull Board board, @NonNull Position from) {
        List<Position> possibleToPositions
            = getPossibleToPositions(from);
        List<Position> filteredToPositions
            = getFilteredToPositions(board, possibleToPositions);

        return filteredToPositions.stream()
            .map(to -> new RegularMove(from, to))
            .collect(Collectors.toList());
    }

    /**
     * Gets the possible positions for the knight to go to
     * from the specified position.
     *
     * @param from The position of the knight piece.
     * @return A list of possible to positions for the knight.
     */
    private List<Position> getPossibleToPositions(Position from) {
        List<Position> toPositions = new ArrayList<>();

        for (Direction hDir : Direction.HORIZONTAL_DIRS) {
            for (Direction vDir : Direction.VERTICAL_DIRS) {
                toPositions.add(from.add(hDir).add(hDir).add(vDir));
                toPositions.add(from.add(vDir).add(vDir).add(hDir));
            }
        }

        return toPositions;
    }

    /**
     * Filters the possible to positions based on board boundaries and piece color.
     *
     * @param board The chessboard.
     * @param toPositions The list of possible to positions.
     * @return A list of filtered to positions for the knight.
     */
    private List<Position> getFilteredToPositions(Board board,
                                                  List<Position> toPositions) {
        return toPositions.stream()
            .filter(toPos -> {
                if (board.isOnBoard(toPos)) {
                    Piece toPiece = board.getPiece(toPos);
                    return !(toPiece != null && toPiece.getColor() == color);
                }
                return false;
            })
            .toList();
    }

    @Override
    public Piece makeCopy() {
        Knight n = new Knight(color);
        n.hasMoved = hasMoved;
        return n;
    }
}
