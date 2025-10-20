package dataaccess;

import object.UserData;

public interface UserDAO {
    boolean addUser(UserData myUserData);

    UserData getUser(String username);

    void deleteUser(String username);

    void clear();
}
