package handlers;

import io.javalin.http.Context;
import requests.JoinGameRequest;
import services.JoinGameService;
import services.ServiceException;

import java.util.Map;

public class JoinGameHandler {
    JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService myJoinGameService) {
        joinGameService = myJoinGameService;
    }

    public void handleRequest(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class);

            joinGameService.joinGame(authToken, req);

            ctx.status(200);
            ctx.json(Map.of());

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
