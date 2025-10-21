package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import object.AuthData;
import object.GameData;
import responses.ListGameArrayResult;
import responses.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListGameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public ListGameService(AuthDAO myAuthDAO, GameDAO myGameDAO) {
        authDAO = myAuthDAO;
        gameDAO = myGameDAO;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        //Check Auth
        AuthData myAuth = authDAO.getAuth(authToken);
        if (myAuth == null) {
            throw new ServiceException(401, "unauthorized");
        }


        Collection<GameData> allGames = gameDAO.listGame();


        List<ListGameArrayResult> gameResults = new ArrayList<>();
        for (GameData game : allGames) {
            gameResults.add(new ListGameArrayResult(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }

        return new ListGamesResult(gameResults);
    }
}
