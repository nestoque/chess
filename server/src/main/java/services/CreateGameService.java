package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import object.AuthData;
import object.GameData;
import requests.CreateGameRequest;
import responses.CreateGameResult;

public class CreateGameService {
    AuthDAO authDAO;
    GameDAO gameDAO;
    int nextGameID;

    public CreateGameService(AuthDAO myAuthDAO, GameDAO myGameDAO) {
        authDAO = myAuthDAO;
        gameDAO = myGameDAO;
        nextGameID = 1;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws ServiceException {
        if (req.gameName() == null || req.gameName().isEmpty()) {
            throw new ServiceException(400, "bad request");
        }

        //Check Auth
        AuthData myAuth = authDAO.getAuth(authToken);
        if (myAuth == null) {
            throw new ServiceException(401, "unauthorized");
        }

        //Check Game Exists
        GameData newGame = new GameData(nextGameID++, null, null, req.gameName(), new ChessGame());
        int newGameID = gameDAO.addGame(newGame);
        if (newGameID == -1) {
            throw new ServiceException(400, "bad request");
        }

        return new CreateGameResult(newGameID);

    }
}
