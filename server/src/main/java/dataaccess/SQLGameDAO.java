package dataaccess;

import chess.ChessGame;

import com.google.gson.Gson;

import object.GameData;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


import static dataaccess.DatabaseManager.getConnection;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO {
    private static final Gson myGson = new Gson();

    public SQLGameDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void configureDatabase() {
        String createTableSQL = """
                CREATE TABLE  IF NOT EXISTS game (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game longtext NOT NULL,
                    PRIMARY KEY (gameID)
                )""";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int addGame(GameData myGameData) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES(?,?,?,?)"
                    , RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, myGameData.whiteUsername());
                preparedStatement.setString(2, myGameData.blackUsername());
                preparedStatement.setString(3, myGameData.gameName());
                preparedStatement.setString(4,
                        myGson.toJson(myGameData.game()));

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }

                return ID;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                myGson.fromJson(rs.getString("game"), ChessGame.class)

                        );
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GameData> listGame() {
        try (var conn = getConnection()) {
            var gamesList = new ArrayList<GameData>();
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game "
            )) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gamesList.add(new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                myGson.fromJson(rs.getString("game"), ChessGame.class)));
                    }
                }
            }
            return gamesList;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameData myGameData) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID=?")) {
                preparedStatement.setString(1, myGameData.whiteUsername());
                preparedStatement.setString(2, myGameData.blackUsername());
                preparedStatement.setString(3, myGson.toJson(myGameData.game(), ChessGame.class));
                preparedStatement.setInt(4, myGameData.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
