package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requests.CreateGameRequest;
import responses.CreateGameResult;
import service.CreateGameService;
import service.ServiceException;

import java.util.Map;

public class CreateGameHandler {
    CreateGameService createGameService;
    Gson json;

    public CreateGameHandler(CreateGameService myCreateGameService, Gson myJson) {
        createGameService = myCreateGameService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest req = json.fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameResult result = createGameService.createGame(authToken, req);

            ctx.status(200);
            ctx.json(json.toJson(result));

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(json.toJson((Map.of("message", "Error: " + e.getMessage()))));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
