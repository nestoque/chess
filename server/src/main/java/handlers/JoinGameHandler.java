package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requests.JoinGameRequest;
import services.JoinGameService;
import services.ServiceException;

import java.util.Map;

public class JoinGameHandler {
    JoinGameService joinGameService;
    Gson json;

    public JoinGameHandler(JoinGameService myJoinGameService, Gson myJson) {
        joinGameService = myJoinGameService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest req = json.fromJson(ctx.body(), JoinGameRequest.class); //Change request DAO all to have String instead :(

            joinGameService.joinGame(authToken, req);

            ctx.status(200);
            ctx.json(json.toJson(Map.of()));

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(json.toJson((Map.of("message", "Error: " + e.getMessage()))));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(json.toJson((Map.of("message", "Error: " + e.getMessage()))));
        }
    }

}
