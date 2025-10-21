package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requests.LoginRequest;

import responses.LoginResult;
import service.LoginService;
import service.ServiceException;

import java.util.Map;

public class LoginHandler {
    LoginService loginService;
    Gson json;

    public LoginHandler(LoginService myLoginService, Gson myJson) {
        loginService = myLoginService;
        json = myJson;
    }

    public void handleRequest(Context ctx) {
        ctx.contentType("application/json");
        try {
            LoginRequest req = json.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = loginService.login(req);

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
