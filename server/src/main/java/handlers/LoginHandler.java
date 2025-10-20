package handlers;

import io.javalin.http.Context;
import requests.CreateGameRequest;
import requests.LoginRequest;
import responses.CreateGameResult;
import responses.LoginResult;
import services.LoginService;
import services.LogoutService;
import services.ServiceException;

import java.util.Map;

public class LoginHandler {
    LoginService loginService;

    public LoginHandler(LoginService myLoginService) {
        loginService = myLoginService;
    }

    public void handleRequest(Context ctx) {
        try {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);

            LoginResult result = loginService.login(req);

            ctx.status(200);
            ctx.json(result);

        } catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.json(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
