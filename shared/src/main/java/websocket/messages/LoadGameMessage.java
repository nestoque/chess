package websocket.messages;

import object.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData gameData;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        gameData = game;
    }

    public GameData getGameData() {
        return gameData;
    }
}
