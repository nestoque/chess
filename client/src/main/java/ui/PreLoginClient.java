package ui;

import exception.ResponseException;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResult;
import responses.RegisterResult;
import serverfacade.ServerFacade;


import java.util.Arrays;

public class PreLoginClient {
    private String authToken;
    private final ServerFacade server;

    public PreLoginClient(ServerFacade mainServer) throws ResponseException {
        server = mainServer;
    }


    public ReplResult eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "l" -> login(params);
                case "r" -> register(params);
                case "q", "quit" -> new ReplResult("quit", ReplResult.State.PRELOGIN);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.PRELOGIN);
        }
    }

    public ReplResult login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResult res = server.login(new LoginRequest(params[0], params[1]));
            authToken = res.authToken();
            return new ReplResult(String.format("You signed in as %s.\n", res.username()), ReplResult.State.POSTLOGIN);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Username> <Password>");
    }

    public ReplResult register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterResult res = server.register(new RegisterRequest(params[0], params[1], params[2]));
            authToken = res.authToken();
            return new ReplResult(String.format("You registered and signed in as %s.\n", res.username()), ReplResult.State.POSTLOGIN);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Username> <Password> <Email>");
    }

    public ReplResult help() {
        return new ReplResult("""
                Options
                l <Username> <Password> - Login an existing User
                r <Username> <Password> <Email> - Register a new user
                h - Print this Help message again
                q - Quit Exits the program.
                """, ReplResult.State.PRELOGIN);
    }

    public String getAuthToken() {
        return authToken;
    }
}

