package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import services.ClearService;

import java.util.Map;

public class ClearHandler {
    ClearService clearService;

    public ClearHandler(ClearService myClearService) {
        clearService = myClearService;
    }


    public void handleRequest(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.json(Map.of());
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
