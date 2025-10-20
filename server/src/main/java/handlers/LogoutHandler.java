package handlers;

import io.javalin.http.Context;
import requests.CreateGameRequest;
import responses.CreateGameResult;
import services.LogoutService;
import services.ServiceException;

import java.util.Map;

public class LogoutHandler {
    LogoutService logoutService;

    public LogoutHandler(LogoutService myLogoutService) {
        logoutService = myLogoutService;
    }

    public void handleRequest(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            logoutService.logout(authToken);

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
