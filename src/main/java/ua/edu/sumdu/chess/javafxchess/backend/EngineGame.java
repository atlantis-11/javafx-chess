package ua.edu.sumdu.chess.javafxchess.backend;

import eventemitter.EventEmitter;
import javafx.application.Platform;
import lombok.NonNull;
import ua.edu.sumdu.chess.javafxchess.backend.events.StockfishErrorEvent;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Extends the base game functionality to include engine-driven gameplay.
 */
public class EngineGame extends Game {
    private final Player humanPlayer;
    private final Stockfish stockfish;
    private final EventEmitter<StockfishErrorEvent> stockfishErrorEventEmitter
        = new EventEmitter<>();

    /**
     * Constructs an EngineGame with the specified parameters.
     *
     * @param humanPlayerPieceColor The color of the human player's pieces.
     * @param engineSkillLevel The skill level of the Stockfish engine.
     */
    public EngineGame(@NonNull PieceColor humanPlayerPieceColor,
                      int engineSkillLevel) {
        humanPlayer = humanPlayerPieceColor == PieceColor.WHITE
            ? playerW
            : playerB;
        stockfish = new Stockfish(engineSkillLevel);
    }

    /**
     * Starts the game, initializing the board and starting the Stockfish engine.
     */
    @Override
    public void start() {
        super.start();

        try {
            stockfish.start();
        } catch (IOException e) {
            emitStockfishErrorEvent();
        }

        if (humanPlayer.getPieceColor() == PieceColor.BLACK) {
            makeEngineMove();
        }
    }

    /**
     * Stops the game and shuts down the Stockfish engine.
     */
    @Override
    public void stop() {
        if (isGameInProgress) {
            super.stop();
            stockfish.stop();
        }
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

    /**
     * Overrides the drawByAgreement method to do nothing,
     * as draw agreements are not applicable in engine games.
     */
    @Override
    public void drawByAgreement() { }

    @Override
    public void resign() {
        currentPlayer = humanPlayer;
        super.resign();
    }

    /** Gets move from Stockfish and makes it. */
    private void makeEngineMove() {
        stockfish.getBestMove(getBoard().getFEN())
            .thenAccept(strMove -> {
                Position from = stockfishCoordToPosition(strMove.substring(0, 2));
                Position to = stockfishCoordToPosition(strMove.substring(2, 4));
                PieceType promotionPieceType = getStockfishPromotionPieceType(strMove);

                Platform.runLater(() -> super.makeMove(from, to, promotionPieceType));
            })
            .exceptionally(ex -> {
                Platform.runLater(this::emitStockfishErrorEvent);
                return null;
            });
    }

    /** Converts Stockfish string coordinate to Position */
    private Position stockfishCoordToPosition(String coord) {
        return new Position(
            '8' - coord.charAt(1),
            coord.charAt(0) - 'a'
        );
    }

    /**
     * Determines the promotion piece type for a given Stockfish move string.
     *
     * @param strMove The Stockfish move string.
     * @return The promotion piece type, or null if no promotion.
     */
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

    /** Checks if it is human player's turn */
    private boolean isHumanCurrentPlayer() {
        return humanPlayer.equals(currentPlayer);
    }

    /** Registers a callback for Stockfish error events. */
    public void onStockfishErrorEvent(Consumer<StockfishErrorEvent> c) {
        stockfishErrorEventEmitter.addConsumer(c);
    }

    /** Emits a Stockfish error event. */
    private void emitStockfishErrorEvent() {
        stop();
        stockfishErrorEventEmitter.trigger(new StockfishErrorEvent());
    }
}
