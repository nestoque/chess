package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResult;
import responses.RegisterResult;
import services.RegisterService;
import services.ServiceException;

import java.util.Map;

public class RegisterHandler {
    RegisterService registerService;
    Gson json;

    public RegisterHandler(RegisterService myRegisterService, Gson myJson) {
        registerService = myRegisterService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            RegisterRequest req = json.fromJson(ctx.body(), RegisterRequest.class);

            RegisterResult result = registerService.register(req);

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
