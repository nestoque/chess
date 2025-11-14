package dataaccess;

import object.GameData;

import java.util.Collection;

public interface GameDAO {
    int addGame(GameData myGameData);

    GameData getGame(int gameID);

    Collection<GameData> listGame();

    boolean updateGame(GameData myGameData);

    void clear();
}
