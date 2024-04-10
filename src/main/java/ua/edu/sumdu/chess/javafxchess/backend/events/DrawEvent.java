package ua.edu.sumdu.chess.javafxchess.backend.events;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class DrawEvent {
    private final DrawReason reason;

    public DrawEvent(@NonNull DrawReason reason) {
        this.reason = reason;
    }
}
