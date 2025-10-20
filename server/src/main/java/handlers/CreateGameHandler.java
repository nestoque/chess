package handlers;

import io.javalin.http.Context;
import requests.CreateGameRequest;
import responses.CreateGameResult;
import services.CreateGameService;
import services.ServiceException;

import java.util.Map;

public class CreateGameHandler {
    CreateGameService createGameService;

    public CreateGameHandler(CreateGameService myCreateGameService) {
        createGameService = myCreateGameService;
    }

    public void handleRequest(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest req = ctx.bodyAsClass(CreateGameRequest.class);

            CreateGameResult result = createGameService.createGame(authToken, req);

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
