package handlers;

import io.javalin.http.Context;
import requests.CreateGameRequest;
import responses.CreateGameResult;
import responses.ListGamesResult;
import services.ListGameService;
import services.ServiceException;

import java.util.Map;

public class ListGamesHandler {
    ListGameService listGameService;

    public ListGamesHandler(ListGameService myListGameService) {
        listGameService = myListGameService;
    }

    public void handleRequest(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ListGamesResult result = listGameService.listGames(authToken);

            ctx.status(200);
            ctx.json(result);

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
