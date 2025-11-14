package ui;

import exception.ResponseException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private String authToken;
    private int joinedGameID;
    private final ServerFacade server;

    public PostLoginClient(String serverUrl, PreLoginClient preClient) throws ResponseException {
        server = new ServerFacade(serverUrl);
        authToken = preClient.getAuthToken();
    }


    public ReplResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "g, list, listgames" -> list(params);
                case "c, create, creategame" -> create(params);
                case "j, join" -> join(params);
                case "w, watch" -> watch(params);
                case "l", "logout" -> new ReplResult("quit", ReplResult.State.PRELOGIN);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.PRELOGIN);
        }
    }

    public ReplResult list(String... params) throws ResponseException {
        responses.ListGamesResult games = server.listGames(authToken);
        StringBuilder outString = new StringBuilder();
        for (ListGameArrayResult game : games.games()) {
            outString.append(String.format("ID: %d\t Name: %s\t White: %s\t Black: %s\n", game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername()));
        }

        return new ReplResult(outString.toString(), ReplResult.State.POSTLOGIN);

    }

    public ReplResult create(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult res = server.createGame(authToken, new CreateGameRequest(params[0]));
            return new ReplResult(String.format("Game ID # %d created.", res.gameID()), ReplResult.State.POSTLOGIN);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Game Name>");
    }

    public ReplResult join(String... params) throws ResponseException {
        if (params.length == 2) {
            server.joinGame(authToken, new JoinGameRequest(params[1], Integer.parseInt(params[0])));
            return new ReplResult(String.format("Join Game #%s as %s.", params[0], params[1]), ReplResult.State.GAME);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Game ID #> <WHITE/BLACK>");
    }

    public ReplResult watch(String... params) throws ResponseException {
        if (params.length == 1) {
            return new ReplResult(String.format("Game ID # %s created.", params[0]), ReplResult.State.GAME);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Game ID #>");
    }

    public ReplResult help() {
        return new ReplResult("""
                Options
                g - List current games
                c <Game Name> - Create a new game
                j <Game ID #> <Team Color> - Join an existing game
                w <Game ID # > - Watch a game
                h - Print this Help message again
                logout - Logout out of chess
                
                """, ReplResult.State.PRELOGIN);
    }

    public int getJoinedGameID() {
        return joinedGameID;
    }
}


