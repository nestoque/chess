package dataaccess;

import object.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    final private HashMap<Integer, GameData> gameHashMap = new HashMap<>();

    @Override
    public int addGame(GameData myGameData) {
        return (gameHashMap.putIfAbsent(myGameData.gameID(), myGameData) == null) ? myGameData.gameID() : -1;
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
    public void deleteGame(int gameID) {
        gameHashMap.remove(gameID);
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
