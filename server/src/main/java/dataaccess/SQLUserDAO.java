package dataaccess;

import object.AuthData;
import object.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;

public class SQLUserDAO implements UserDAO {
    Object BCrypt;

    public SQLUserDAO() {
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
                CREATE TABLE  IF NOT EXISTS user (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username)
                )""";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addUser(UserData myUserData) {
        try (var conn = getConnection()) {


            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, myUserData.username());

                preparedStatement.setString(2, myUserData.password());// needs to be encrypted
                preparedStatement.setString(3, myUserData.email());

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
    public UserData getUser(String username) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"), rs.getString("email"));
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
    public void clear() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE user")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
