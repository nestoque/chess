package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import services.ClearService;

import java.util.Map;

public class ClearHandler {
    ClearService clearService;
    Gson json;

    public ClearHandler(ClearService myClearService, Gson myJson) {
        clearService = myClearService;
        json = myJson;
    }


    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            clearService.clear();
            ctx.status(200);
            ctx.json(json.toJson(Map.of()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(json.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
