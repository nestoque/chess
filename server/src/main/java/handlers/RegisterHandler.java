package handlers;

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

    public RegisterHandler(RegisterService myRegisterService) {
        registerService = myRegisterService;
    }

    public void handleRequest(Context ctx) {
        try {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);

            RegisterResult result = registerService.register(req);

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
