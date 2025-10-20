package dataaccess;

import object.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final static private HashMap<String, UserData> userHashMap = new HashMap<>();

    @Override
    public boolean addUser(UserData myUserData) {
        return userHashMap.putIfAbsent(myUserData.username(), myUserData) == null;
    }

    @Override
    public UserData getUser(String username) {
        return userHashMap.get(username);
    }

    @Override
    public void deleteUser(String username) {
        userHashMap.remove(username);
    }

    @Override
    public void clear() {
        userHashMap.clear();
    }
}
