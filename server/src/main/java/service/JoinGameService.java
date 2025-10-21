package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import object.AuthData;
import object.GameData;
import requests.JoinGameRequest;

import java.util.Set;

public class JoinGameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    private static final Set<String> TEAM_COLORS = Set.of(new String[]{"WHITE", "BLACK"});

    public JoinGameService(AuthDAO myAuthDAO, GameDAO myGameDAO) {
        authDAO = myAuthDAO;
        gameDAO = myGameDAO;
    }

    public void joinGame(String authToken, JoinGameRequest req) throws ServiceException {
        if (req.playerColor() == null || req.playerColor().isEmpty() ||
                !TEAM_COLORS.contains(req.playerColor()) || req.gameID() < 1) {
            throw new ServiceException(400, "bad request");
        }

        //Check Auth
        AuthData myAuth = authDAO.getAuth(authToken);
        if (myAuth == null) {
            throw new ServiceException(401, "unauthorized");
        }

        //Check Game Exists
        GameData myGame = gameDAO.getGame(req.gameID());
        if (myGame == null) {
            throw new ServiceException(400, "bad request");
        }

        //Check Color
        GameData updatedGame;
        switch (req.playerColor()) {
            case "WHITE" -> {
                if (myGame.whiteUsername() != null) {
                    throw new ServiceException(403, "already taken");
                } else {
                    updatedGame = myGame.setWhiteUsername(myAuth.username());
                }
            }
            case "BLACK" -> {
                if (myGame.blackUsername() != null) {
                    throw new ServiceException(403, "already taken");
                } else {
                    updatedGame = myGame.setBlackUsername(myAuth.username());
                }
            }
            default -> throw new ServiceException(400, "bad request");
        }

        gameDAO.updateGame(updatedGame);
    }
}
