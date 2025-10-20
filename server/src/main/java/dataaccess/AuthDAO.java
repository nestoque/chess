package dataaccess;

import object.AuthData;

public interface AuthDAO {
    boolean addAuth(AuthData myAuthData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clear();
}
