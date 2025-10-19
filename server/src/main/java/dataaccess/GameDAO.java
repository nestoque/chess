package dataaccess;

public interface GameDAO {
    String addGame();

    GameData getGame();

    Collection<GameData> ListGame();

    void deleteGame();

    void clear();
}
