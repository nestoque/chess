package dataaccess;

import object.AuthData;
import dataaccess.DatabaseManager.*;

import java.sql.*;

import static dataaccess.DatabaseManager.getConnection;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() {
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
    public boolean addAuth(AuthData myAuthData) {
        try (var conn = getConnection()) {


            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, myAuthData.authToken());
                preparedStatement.setString(2, myAuthData.username());

                preparedStatement.executeUpdate();

                return true;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            return false;
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
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteAuth(String authToken) {
        try (var conn = getConnection()) {

            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE id=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (var conn = getConnection()) {

            try (var preparedStatement = conn.prepareStatement("TRUNCATE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
