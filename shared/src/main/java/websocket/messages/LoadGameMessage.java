package websocket.messages;

import object.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;

    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        game = gameData;
    }

    public GameData getGameData() {
        return game;
    }
}
