package dataaccess;

import object.UserData;

public class SQLUserDAO implements UserDAO {
    @Override
    public boolean addUser(UserData myUserData) {
        return false;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clear() {

    }
}
