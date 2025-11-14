package ui;

public record ReplResult(
        String message,
        State currentState
) {
    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAME
    }
}
