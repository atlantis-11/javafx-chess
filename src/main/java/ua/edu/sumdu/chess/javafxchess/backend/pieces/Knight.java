package ua.edu.sumdu.chess.javafxchess.backend.pieces;

import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Direction;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.RegularMove;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Knight extends Piece {
    public Knight(PieceColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> getMoves(Board board, Position from) {
        List<Position> possibleToPositions
            = getPossibleToPositions(from);
        List<Position> filteredToPositions
            = getFilteredToPositions(board, possibleToPositions);

        return filteredToPositions.stream()
            .map(to -> new RegularMove(from, to))
            .collect(Collectors.toList());
    }

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
