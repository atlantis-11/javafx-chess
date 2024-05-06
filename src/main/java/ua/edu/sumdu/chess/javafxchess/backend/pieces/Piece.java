package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a piece on the chessboard.
 */
@Getter
public abstract class Piece {
    protected final PieceColor color;
    protected final PieceType type;
    @Setter
    protected boolean hasMoved;

    /**
     * Constructs a piece with the specified color and type.
     *
     * @param color The color of the piece.
     * @param type The type of the piece.
     */
    public Piece(@NonNull PieceColor color,
                 @NonNull PieceType type) {
        this.color = color;
        this.type = type;
    }

    /**
     * Gets the possible moves for the piece from the specified position.
     *
     * @param board The chessboard.
     * @param from The position of the piece.
     * @return A list of possible moves.
     */
    public abstract List<Move> getMoves(Board board, Position from);

    /**
     * Gets the possible moves in specified directions from the current position.
     *
     * @param board The chessboard.
     * @param from The current position of the piece.
     * @param dirs The directions to consider.
     * @return A list of possible moves in the specified directions.
     */
    protected List<Move> getMovesInDirs(Board board, Position from,
                                        Direction[] dirs) {
        List<Move> moves = new ArrayList<>();

        for (Direction dir : dirs) {
            for (Position to = from.add(dir);
                 board.isOnBoard(to);
                 to = to.add(dir)) {
                Piece toPiece = board.getPiece(to);

                if (toPiece != null) {
                    if (toPiece.getColor() != color) {
                        moves.add(new RegularMove(from, to));
                    }

                    break;
                }

                moves.add(new RegularMove(from, to));
            }
        }

        return moves;
    }

    /**
     * Checks if the piece can capture the opponent's king from the specified position.
     *
     * @param board The chessboard.
     * @param from The position of the piece.
     * @return True if the piece can capture the opponent's king, otherwise false.
     */
    public boolean canCaptureKing(@NonNull Board board, @NonNull Position from) {
        return getMoves(board, from).stream()
            .anyMatch(move -> {
                Piece piece = board.getPiece(move.getTo());
                return piece != null && piece.getType() == PieceType.KING;
            });
    }

    /** Creates a deep copy of the piece. */
    public abstract Piece makeCopy();
}
