package dataaccess;

import object.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    final private HashMap<Integer, GameData> gameHashMap = new HashMap<>();
    int nextGameID = 1;

    @Override
    public int addGame(GameData myGameData) {
        if (myGameData.gameID() != 0) {
            return -1;
        }
        GameData numberedGame = myGameData.setGameID(nextGameID);
        return (gameHashMap.putIfAbsent(nextGameID++, numberedGame) == null) ? numberedGame.gameID() : -1;
    }

    @Override
    public GameData getGame(int gameID) {
        return gameHashMap.get(gameID);
    }

    @Override
    public Collection<GameData> listGame() {
        return gameHashMap.values();
    }

    @Override
    public void updateGame(GameData myGameData) {
        gameHashMap.put(myGameData.gameID(), myGameData); //replaces
    }

    @Override
    public void clear() {
        gameHashMap.clear();
    }
}
