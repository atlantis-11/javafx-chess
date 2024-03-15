package ua.edu.sumdu.chess.javafxchess.backend.events;

import lombok.Getter;

@Getter
public class DrawEvent {
    private final DrawReason reason;

    public DrawEvent(DrawReason reason) {
        this.reason = reason;
    }
}
