package ua.edu.sumdu.chess.javafxchess.backend;

import javafx.application.Platform;
import ua.edu.sumdu.chess.javafxchess.backend.events.*;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;
import eventemitter.EventEmitter;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a chess game.
 */
public class Game {
    @Getter
    private Board board;
    protected boolean isGameInProgress = false;
    protected final Player playerW = new Player(PieceColor.WHITE);
    protected final Player playerB = new Player(PieceColor.BLACK);
    protected Player currentPlayer;
    @Getter
    private final int timeInSeconds;
    private Timer timer;
    private final EventEmitter<MoveMadeEvent> moveMadeEventEmitter
        = new EventEmitter<>();
    private final EventEmitter<TimeUpdatedEvent> timeUpdatedEventEmitter
        = new EventEmitter<>();
    private final EventEmitter<WinEvent> winEventEmitter
        = new EventEmitter<>();
    private final EventEmitter<DrawEvent> drawEventEmitter
        = new EventEmitter<>();

    /** Constructs a game with no time limit. */
    public Game() {
        this.timeInSeconds = 0;
    }

    /**
     * Constructs a game with a specified time limit for each player.
     *
     * @param timeInSeconds The time limit for each player in seconds.
     */
    public Game(int timeInSeconds) {
        this.timeInSeconds = Math.max(timeInSeconds, 0);
    }

    /**
     * Starts the game by initializing the board and setting up players.
     */
    public void start() {
        board = new Board();
        board.initialize();

        currentPlayer = playerW;

        if (timeInSeconds > 0) {
            playerW.setTimeLeft(timeInSeconds);
            playerB.setTimeLeft(timeInSeconds);
            startTheClock();
        }

        isGameInProgress = true;
    }

    /** Stops the game. */
    public void stop() {
        if (isGameInProgress) {
            isGameInProgress = false;
            stopTheClock();
        }
    }

    /** Starts the game clock. */
    private void startTheClock() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentPlayer.decrementTimeLeft();
                Platform.runLater(() -> emitTimeUpdatedEvent());

                if (currentPlayer.getTimeLeft() == 0) {
                    Platform.runLater(() -> emitWinEvent(WinReason.TIMEOUT));
                }
            }
        }, 1000, 1000); // Run every second
    }

    /** Stops the game clock. */
    private void stopTheClock() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Gets the legal moves for a piece at the specified position.
     *
     * @param from The position of the piece.
     * @return A list of legal moves for the piece.
     */
    public List<Move> getLegalMoves(@NonNull Position from) {
        if (isGameInProgress) {
            Piece piece = board.getPiece(from);

            if (piece != null
                    && piece.getColor() == currentPlayer.getPieceColor()) {
                return board.getLegalMoves(from);
            }
        }

        return Collections.emptyList();
    }

    /**
     * Makes a move on the board.
     *
     * @param from The starting position of the move.
     * @param to The target position of the move.
     * @param promotionPieceType The type of piece to promote to, if applicable.
     */
    public void makeMove(@NonNull Position from, @NonNull Position to,
                         PieceType promotionPieceType) {
        if (!isGameInProgress) {
            return;
        }

        Piece fromPiece = board.getPiece(from);

        if (fromPiece.getColor() == currentPlayer.getPieceColor()) {
            Move foundMove = findMoveByPositions(from, to);

            if (foundMove != null) {
                checkForPromotionMove(foundMove, promotionPieceType);

                foundMove.execute(board);
                emitMoveMadeEvent();

                if (!checkForGameOver()) {
                    currentPlayer = getOpponent();
                }
            }
        }
    }

    /**
     * Makes a move on the board without promotion.
     *
     * @param from The starting position of the move.
     * @param to The target position of the move.
     */
    public void makeMove(Position from, Position to) {
        makeMove(from, to, null);
    }

    /** Draws the game by agreement. */
    public void drawByAgreement() {
        if (!isGameInProgress) {
            return;
        }

        emitDrawEvent(DrawReason.AGREEMENT);
    }

    /** Resigns the game. */
    public void resign() {
        if (!isGameInProgress) {
            return;
        }

        emitWinEvent(WinReason.RESIGNATION);
    }

    /**
     * Finds a legal move by specified positions.
     *
     * @param from The starting position of the move.
     * @param to The target position of the move.
     * @return The legal move found, or null if no such move exists.
     */
    private Move findMoveByPositions(Position from, Position to) {
        return board.getLegalMoves(from).stream()
            .filter(move -> move.getFrom().equals(from) && move.getTo().equals(to))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if the move is a promotion move and sets the
     * promotion piece type if one was passed.
     *
     * @param move The move to check.
     * @param promotionPieceType The type of piece to promote to, optional.
     */
    private void checkForPromotionMove(Move move, PieceType promotionPieceType) {
        if (promotionPieceType != null
                && move instanceof PromotionMove) {
            ((PromotionMove) move).setPromotionPieceType(promotionPieceType);
        }
    }

    /**
     * Gets opponent of the current player.
     *
     * @return opponent of the current player.
     */
    private Player getOpponent() {
        return currentPlayer == playerW ? playerB : playerW;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if game is over, false otherwise.
     */
    private boolean checkForGameOver() {
        PieceColor opponentColor = getOpponent().getPieceColor();

        if (board.getLegalMoves(opponentColor).isEmpty()) {
            if (board.isInCheck(opponentColor)) {
                emitWinEvent(WinReason.CHECKMATE);
            } else {
                emitDrawEvent(DrawReason.STALEMATE);
            }
        } else if (isThreefoldRepetition()) {
            emitDrawEvent(DrawReason.THREEFOLD_REPETITION);
        } else if (isInsufficientMaterial()) {
            emitDrawEvent(DrawReason.INSUFFICIENT_MATERIAL);
        } else if (board.getHalfmoveClock() == 100) {
            emitDrawEvent(DrawReason.FIFTY_MOVE_RULE);
        } else {
            return false;
        }

        return true;
    }

    /**
     * Checks if game is drawn by threefold repetition
     *
     * @return true if game is drawn by threefold repetition, false otherwise.
     */
    private boolean isThreefoldRepetition() {
        if (!board.getRepetitionFENHistory().isEmpty()) {
            String lastFEN = board.getRepetitionFENHistory().getLast();
            return board.getRepetitionFENHistory()
                .stream()
                .filter(lastFEN::equals)
                .count() == 3;
        }
        return false;
    }

    /**
     * Checks if game is drawn by insufficient material
     *
     * @return true if game is drawn by insufficient material, false otherwise.
     */
    private boolean isInsufficientMaterial() {
        if (board.getPositions().size() == 2) {
            // Both Sides have a bare King
            return true;
        } else if (board.getPositions().size() == 3) {
            // One Side has a King and a Minor Piece against a bare King
            return board.getPositions()
                .stream()
                .map(board::getPiece)
                .anyMatch(p ->
                    p.getType() == PieceType.BISHOP
                    || p.getType() == PieceType.KNIGHT
                );
        } else if (board.getPositions().size() == 4) {
            // Both Sides have a King and a Bishop, the Bishops being the same Color
            List<Position> bishopsPos = board.getPositions()
                .stream()
                .filter(p -> board.getPiece(p).getType() == PieceType.BISHOP)
                .toList();

            return bishopsPos.size() == 2
                && (bishopsPos.get(0).row() + bishopsPos.get(0).col()) % 2 ==
                   (bishopsPos.get(1).row() + bishopsPos.get(1).col()) % 2;
        }
        return false;
    }

    /** Registers a callback for move made events. */
    public void onMoveMade(@NonNull Consumer<MoveMadeEvent> c) {
        moveMadeEventEmitter.addConsumer(c);
    }

    /** Registers a callback for time updated events. */
    public void onTimeUpdated(@NonNull Consumer<TimeUpdatedEvent> c) {
        timeUpdatedEventEmitter.addConsumer(c);
    }

    /** Registers a callback for win events. */
    public void onWin(@NonNull Consumer<WinEvent> c) {
        winEventEmitter.addConsumer(c);
    }

    /** Registers a callback for draw events. */
    public void onDraw(@NonNull Consumer<DrawEvent> c) {
        drawEventEmitter.addConsumer(c);
    }

    /** Emits a move made event. */
    private void emitMoveMadeEvent() {
        moveMadeEventEmitter.trigger(
            new MoveMadeEvent(currentPlayer.getPieceColor())
        );
    }

    /** Emits a time updated event. */
    private void emitTimeUpdatedEvent() {
        timeUpdatedEventEmitter.trigger(
            new TimeUpdatedEvent(
                currentPlayer.getTimeLeft(),
                currentPlayer.getPieceColor()
            )
        );
    }

    /** Emits a win event with the specified reason. */
    private void emitWinEvent(WinReason reason) {
        stop();
        PieceColor winnerColor = switch (reason) {
            case RESIGNATION, TIMEOUT -> getOpponent().getPieceColor();
            default -> currentPlayer.getPieceColor();
        };
        winEventEmitter.trigger(new WinEvent(reason, winnerColor));
    }

    /** Emits a draw event with the specified reason. */
    private void emitDrawEvent(DrawReason reason) {
        stop();
        drawEventEmitter.trigger(new DrawEvent(reason));
    }
}
