package dataaccess;

import object.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public boolean addAuth(AuthData myAuthData) {
        return false;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
