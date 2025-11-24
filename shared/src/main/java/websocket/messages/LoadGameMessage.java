package websocket.messages;

import object.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData gameData;

    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        gameData = game;
    }

    public GameData getGameData() {
        return gameData;
    }
}
