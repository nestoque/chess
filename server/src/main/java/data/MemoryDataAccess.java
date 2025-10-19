package data;

import dataaccess.AuthDAO;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


public class MemoryDataAccess {
    final static private HashMap<String, UserData> userHashMap = new HashMap<>();
    static private int nextUserId = 1;
    final static private HashMap<String, AuthData> authHashMap = new HashMap<>();
    static private int nextAuthId = 1;
    final static private HashMap<String, GameData> gameHashMap = new HashMap<>();
    static private int nextGameId = 1;

    //User
    static public UserData addUser(UserData myUserData) {
        return userHashMap.put(myUserData.username(), myUserData);
    }

    static public UserData getUser(String username) {
        return userHashMap.get(username);
    }

    static public Collection<UserData> listUser() {
        return userHashMap.values();
    }

    static public UserData deleteUser(String username) {
        return userHashMap.remove(username);
    }

    //Auth
    static public AuthData addAuth(AuthData myAuthData) {
        return authHashMap.put(myAuthData.authToken(), myAuthData);
    }

    static public AuthData getAuth(String authToken) {
        return authHashMap.get(authToken);
    }

    static public Collection<AuthData> listAuth() {
        return authHashMap.values();
    }

    static public AuthData deleteAuth(String authToken) {
        return authHashMap.remove(authToken);
    }


    //Game


    static public Collection<GameData> listGames() {
        return gameHashMap.values();
    }


}
