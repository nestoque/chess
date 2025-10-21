package service;

import dataaccess.*;

public class ClearService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public ClearService(UserDAO myuserDAO, AuthDAO myauthDAO, GameDAO mygameDAO) {
        userDAO = myuserDAO;
        authDAO = myauthDAO;
        gameDAO = mygameDAO;
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
