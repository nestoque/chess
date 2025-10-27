package dataaccess;

import object.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public int addGame(GameData myGameData) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGame() {
        return List.of();
    }

    @Override
    public void updateGame(GameData myGameData) {

    }

    @Override
    public void clear() {

    }
}
