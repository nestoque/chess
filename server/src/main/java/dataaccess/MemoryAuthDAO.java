package dataaccess;

import object.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final static private HashMap<String, AuthData> authHashMap = new HashMap<>();

    @Override
    public boolean addAuth(AuthData myAuthData) {
        return authHashMap.putIfAbsent(myAuthData.authToken(), myAuthData) == null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authHashMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authHashMap.remove(authToken);
    }

    @Override
    public void clear() {
        authHashMap.clear();
    }
}
