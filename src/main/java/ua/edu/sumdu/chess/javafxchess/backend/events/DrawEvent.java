package ua.edu.sumdu.chess.javafxchess.backend.events;

import lombok.Getter;

@Getter
public class DrawEvent {
    DrawReason reason;

    public DrawEvent(DrawReason reason) {
        this.reason = reason;
    }
}
