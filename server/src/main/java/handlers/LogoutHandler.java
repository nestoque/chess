package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import services.LogoutService;
import services.ServiceException;

import java.util.Map;

public class LogoutHandler {
    LogoutService logoutService;
    Gson json;

    public LogoutHandler(LogoutService myLogoutService, Gson myJson) {
        logoutService = myLogoutService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            String authToken = ctx.header("authorization");

            logoutService.logout(authToken);

            ctx.status(200);
            ctx.json(json.toJson(Map.of()));

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
