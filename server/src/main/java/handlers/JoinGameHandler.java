package handlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.javalin.http.BadRequestResponse;
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
            JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class); //Change request DAO all to have String instead :(

            joinGameService.joinGame(authToken, req);

            ctx.status(200);
            ctx.json(Map.of());

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            System.err.println("The real exception type is: " + e.getClass().getName());
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
