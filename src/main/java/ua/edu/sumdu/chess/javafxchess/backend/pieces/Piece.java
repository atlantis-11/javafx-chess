package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Piece {
    protected final PieceColor color;
    protected final PieceType type;
    @Setter
    protected boolean hasMoved;

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public abstract List<Move> getMoves(Board board, Position from);

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

    public boolean canCaptureKing(Board board, Position from) {
        return getMoves(board, from).stream()
            .anyMatch(move -> {
                Piece piece = board.getPiece(move.getTo());
                return piece != null && piece.getType() == PieceType.KING;
            });
    }

    public abstract Piece makeCopy();
}
