package services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import object.AuthData;
import object.UserData;
import requests.RegisterRequest;
import responses.RegisterResult;
import utils.TokenUtils;

public class RegisterService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public RegisterService(UserDAO myUserDAO, AuthDAO myAuthDAO) {
        userDAO = myUserDAO;
        authDAO = myAuthDAO;
    }

    public RegisterResult register(RegisterRequest req) throws ServiceException {
        if (req.username() == null || req.username().isEmpty() ||
                req.password() == null || req.password().isEmpty() ||
                req.email() == null || req.email().isEmpty()) {

            throw new ServiceException(400, "bad request");
        }

        //Add if new
        UserData newUser = new UserData(req.username(), req.password(), req.email());

        if (!userDAO.addUser(newUser)) {
            throw new ServiceException(403, "already taken");
        }


        //logins step?
        AuthData newAuth = new AuthData(TokenUtils.generateToken(), req.username());
        if (!authDAO.addAuth(newAuth)) {
            throw new ServiceException(500, "Internal error: Failed to create auth token");
        }

        return new RegisterResult(newAuth.username(), newAuth.authToken());
    }

}
