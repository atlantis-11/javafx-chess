package ua.edu.sumdu.chess.javafxchess.backend;

import javafx.application.Platform;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class EngineGame extends Game {
    private final Player humanPlayer;
    private final Stockfish stockfish;

    public EngineGame(PieceColor humanPlayerPieceColor, int engineSkillLevel) {
        humanPlayer = humanPlayerPieceColor == PieceColor.WHITE
            ? playerW
            : playerB;
        stockfish = new Stockfish(engineSkillLevel);
    }

    @Override
    public void start() {
        super.start();

        try {
            stockfish.start();
        } catch (IOException ignored) { }

        if (humanPlayer.getPieceColor() == PieceColor.BLACK) {
            makeEngineMove();
        }
    }

    @Override
    protected void stop() {
        super.stop();
        try {
            stockfish.stop();
        } catch (IOException ignored) { }
    }

    @Override
    public List<Move> getLegalMoves(Position from) {
        if (isHumanCurrentPlayer()) {
            return super.getLegalMoves(from);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void makeMove(Position from, Position to,
                         PieceType promotionPieceType) {
        if (isHumanCurrentPlayer()) {
            super.makeMove(from, to, promotionPieceType);

            if (!isHumanCurrentPlayer()) {
                makeEngineMove();
            }
        }
    }

    @Override
    public void drawByAgreement() { }

    @Override
    public void resign() {
        currentPlayer = humanPlayer;
        super.resign();
    }

    private void makeEngineMove() {
        new Thread(() -> {
            try {
                String strMove = stockfish.getBestMove(getBoard().getFEN(), 200);

                Position from = stockfishCoordToPosition(strMove.substring(0, 2));
                Position to = stockfishCoordToPosition(strMove.substring(2, 4));
                PieceType promotionPieceType = getStockfishPromotionPieceType(strMove);

                Platform.runLater(() -> super.makeMove(from, to, promotionPieceType));
            } catch (Exception ignored) { }
        }).start();
    }

    private Position stockfishCoordToPosition(String coord) {
        return new Position(
            '8' - coord.charAt(1),
            coord.charAt(0) - 'a'
        );
    }

    private PieceType getStockfishPromotionPieceType(String strMove) {
        if (strMove.length() == 5) {
            return switch (strMove.charAt(4)) {
                case 'n' -> PieceType.KNIGHT;
                case 'r' -> PieceType.ROOK;
                case 'b' -> PieceType.BISHOP;
                default -> PieceType.QUEEN;
            };
        }

        return null;
    }

    private boolean isHumanCurrentPlayer() {
        return humanPlayer.equals(currentPlayer);
    }
}
