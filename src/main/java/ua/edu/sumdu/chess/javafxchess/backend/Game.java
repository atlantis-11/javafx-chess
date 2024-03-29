package ua.edu.sumdu.chess.javafxchess.backend;

import javafx.application.Platform;
import ua.edu.sumdu.chess.javafxchess.backend.events.*;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.*;
import eventemitter.EventEmitter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Game {
    @Getter
    private Board board;
    private final Player playerW = new Player(PieceColor.WHITE);
    private final Player playerB = new Player(PieceColor.BLACK);
    private Player currentPlayer;
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

    public Game() {
        this.timeInSeconds = 0;
    }

    public Game(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public void start() {
        board = new Board();
        board.initialize();

        currentPlayer = playerW;

        if (timeInSeconds > 0) {
            playerW.setTimeLeft(timeInSeconds);
            playerB.setTimeLeft(timeInSeconds);
            startTheClock();
        }
    }

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

    private void stopTheClock() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public List<Move> getLegalMoves(Position from) {
        Piece piece = board.getPiece(from);

        if (piece != null
                && piece.getColor() == currentPlayer.getPieceColor()) {
            return board.getLegalMoves(from);
        }

        return new ArrayList<>();
    }

    public void makeMove(Position from, Position to,
                         PieceType promotionPieceType) {
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

    public void makeMove(Position from, Position to) {
        makeMove(from, to, null);
    }

    public void drawByAgreement() {
        emitDrawEvent(DrawReason.AGREEMENT);
    }

    public void resign() {
        emitWinEvent(WinReason.RESIGNATION);
    }

    private Move findMoveByPositions(Position from, Position to) {
        return board.getLegalMoves(from).stream()
            .filter(move -> move.getFrom().equals(from) && move.getTo().equals(to))
            .findFirst()
            .orElse(null);
    }

    private void checkForPromotionMove(Move move, PieceType promotionPieceType) {
        if (promotionPieceType != null
                && move instanceof PromotionMove) {
            ((PromotionMove) move).setPromotionPieceType(promotionPieceType);
        }
    }

    private Player getOpponent() {
        return currentPlayer == playerW ? playerB : playerW;
    }

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

    public void onMoveMade(Consumer<MoveMadeEvent> c) {
        moveMadeEventEmitter.addConsumer(c);
    }

    public void onTimeUpdated(Consumer<TimeUpdatedEvent> c) {
        timeUpdatedEventEmitter.addConsumer(c);
    }

    public void onWin(Consumer<WinEvent> c) {
        winEventEmitter.addConsumer(c);
    }

    public void onDraw(Consumer<DrawEvent> c) {
        drawEventEmitter.addConsumer(c);
    }

    private void emitMoveMadeEvent() {
        moveMadeEventEmitter.trigger(
            new MoveMadeEvent(currentPlayer.getPieceColor())
        );
    }

    private void emitTimeUpdatedEvent() {
        timeUpdatedEventEmitter.trigger(
            new TimeUpdatedEvent(
                currentPlayer.getTimeLeft(),
                currentPlayer.getPieceColor()
            )
        );
    }

    private void emitWinEvent(WinReason reason) {
        stopTheClock();
        PieceColor winnerColor = switch (reason) {
            case RESIGNATION, TIMEOUT -> getOpponent().getPieceColor();
            default -> currentPlayer.getPieceColor();
        };
        winEventEmitter.trigger(new WinEvent(reason, winnerColor));
    }

    private void emitDrawEvent(DrawReason reason) {
        stopTheClock();
        drawEventEmitter.trigger(new DrawEvent(reason));
    }
}
