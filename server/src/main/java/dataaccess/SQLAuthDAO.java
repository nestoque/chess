package dataaccess;

import object.AuthData;
import dataaccess.DatabaseManager.*;

import java.sql.*;

import static dataaccess.DatabaseManager.getConnection;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() {
        try {
            configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void configureDatabase() throws Exception {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                );
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
            ps.executeUpdate();
        }
    }

    @Override
    public boolean addAuth(AuthData myAuthData) throws SQLException {
        try (var conn = getConnection()) {


            try (var preparedStatement = conn.prepareStatement("INSERT INTO chess (authToken, useranme) VALUES(?, ?)")) {
                preparedStatement.setString(1, myAuthData.authToken());
                preparedStatement.setString(2, myAuthData.username());

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var authToken = myAuthData.authToken();
                if (resultSet.next()) {
                    if (resultSet.getString(authToken)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username, type FROM auth WHERE type=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),
                                rs.getString("username"));
                    } else {
                        return null;
                    }
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
