package service;

import dataaccess.AuthDAO;
import object.AuthData;

public class LogoutService {
    AuthDAO authDAO;

    public LogoutService(AuthDAO myAuthDAO) {
        authDAO = myAuthDAO;
    }

    public void logout(String authToken) throws ServiceException {
        //Check Auth
        AuthData myAuth = authDAO.getAuth(authToken);
        if (myAuth == null) {
            throw new ServiceException(401, "unauthorized");
        }

        //Delete Auth
        authDAO.deleteAuth(authToken);
    }
}
