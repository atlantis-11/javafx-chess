package ua.edu.sumdu.chess.javafxchess.backend.events;

public enum DrawReason {
    STALEMATE,
    INSUFFICIENT_MATERIAL,
    FIFTY_MOVE_RULE,
    THREEFOLD_REPETITION,
    AGREEMENT
}
