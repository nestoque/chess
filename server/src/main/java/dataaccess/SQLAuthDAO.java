package dataaccess;

import object.AuthData;
import dataaccess.DatabaseManager.*;

import java.sql.*;

import static dataaccess.DatabaseManager.getConnection;

public class SQLAuthDAO implements AuthDAO {
    private final static String UUIDREGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    

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
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")) {
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
            if (authToken.matches(SQLAuthDAO.UUIDREGEX)) {
                try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                    preparedStatement.setString(1, authToken);
                    preparedStatement.executeUpdate();
                }
            } else {
                throw new RuntimeException("not valid UUID");
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
