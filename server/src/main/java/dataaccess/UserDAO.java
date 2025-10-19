package dataaccess;

import object.UserData;

public interface UserDAO {
    String addUser();

    UserData getUser();

    void deleteUser();

    void clear() {

    }

    ;
}
