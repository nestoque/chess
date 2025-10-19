package services;

import dataaccess.MemoryAuthDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class ClearService {
    UserDAO clearUserAccess = new UserDAO();
    AuthDAO clearAuthAccess = new MemoryAuthDAO();
    GameDAO clearGameAccess = new GameDAO();
        clearUserAccess.clear();
        clearAuthAccess.clear();
        clearGameAccess.clear();
}
