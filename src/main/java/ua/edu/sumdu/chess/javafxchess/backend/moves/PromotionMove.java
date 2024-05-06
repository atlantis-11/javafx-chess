package ua.edu.sumdu.chess.javafxchess.backend.moves;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;
import lombok.Getter;

/**
 * Represents a promotion move in the chess game.
 */
@Getter
public class PromotionMove extends Move {
    private PieceType promotionPieceType = PieceType.QUEEN;

    /** Constructs a promotion move with the
     * specified 'from' and 'to' positions. */
    public PromotionMove(Position from, Position to) {
        super(from, to);
    }

    /**
     * Sets the promotion piece type.
     *
     * @param pieceType The type of piece for promotion.
     */
    public void setPromotionPieceType(PieceType pieceType) {
        if (pieceType == PieceType.QUEEN
                || pieceType == PieceType.ROOK
                || pieceType == PieceType.BISHOP
                || pieceType == PieceType.KNIGHT) {
            this.promotionPieceType = pieceType;
        }
    }

    /**
     * Creates the promotion piece based on the specified color.
     *
     * @param color The color of the promotion piece.
     * @return The promotion piece.
     */
    private Piece getPromotionPiece(PieceColor color) {
        return switch (promotionPieceType) {
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            default -> new Queen(color);
        };
    }

    @Override
    protected void doExecute(Board board) {
        Piece pawn = board.getPiece(from);
        Piece promotionPiece = getPromotionPiece(pawn.getColor());
        promotionPiece.setHasMoved(true);

        board.setPiece(to, promotionPiece);
        board.setPiece(from, null);
    }
}
