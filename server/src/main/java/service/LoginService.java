package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import object.AuthData;
import object.UserData;
import requests.LoginRequest;
import responses.LoginResult;
import utils.TokenUtils;

import java.util.Objects;

public class LoginService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public LoginService(UserDAO myUserDAO, AuthDAO myAuthDAO) {
        userDAO = myUserDAO;
        authDAO = myAuthDAO;
    }

    public LoginResult login(LoginRequest req) throws ServiceException {
        if (req.username() == null || req.username().isEmpty() ||
                req.password() == null || req.password().isEmpty()) {
            throw new ServiceException(400, "bad request");
        }

        //User not found
        UserData user = userDAO.getUser(req.username());
        if (user == null) {
            throw new ServiceException(401, "unauthorized");
        }

        //Wrong password
        if (!Objects.equals(user.password(), req.password())) {
            throw new ServiceException(401, "unauthorized");
        }

        //Make AuthData
        AuthData newAuth = new AuthData(TokenUtils.generateToken(), req.username());
        if (!authDAO.addAuth(newAuth)) {
            throw new ServiceException(500, "Internal error: Failed to create auth token");
        }

        //return AuthToken
        return new LoginResult(newAuth.username(), newAuth.authToken());
    }

}
