package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import responses.ListGamesResult;
import services.ListGameService;
import services.ServiceException;

import java.util.Map;

public class ListGamesHandler {
    ListGameService listGameService;
    Gson json;

    public ListGamesHandler(ListGameService myListGameService, Gson myJson) {
        listGameService = myListGameService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            String authToken = ctx.header("authorization");

            ListGamesResult result = listGameService.listGames(authToken);

            ctx.status(200);
            ctx.json(json.toJson(result));

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
