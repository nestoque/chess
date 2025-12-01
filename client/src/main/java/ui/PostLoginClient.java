package ui;

import exception.ResponseException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.*;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private String authToken;
    private int joinedGameID;
    private String joinedColor = null;
    private final ServerFacade server;
    private final PreLoginClient preClient;

    public PostLoginClient(ServerFacade mainServer, PreLoginClient preClient) throws ResponseException {
        server = mainServer;

        this.preClient = preClient;
    }


    public ReplResult eval(String input) {
        try {
            authToken = preClient.getAuthToken();
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "g", "list", "listgames" -> list(params);
                case "c", "create", "creategame" -> create(params);
                case "j", "join" -> join(params);
                case "w", "watch" -> watch(params);
                case "l", "logout" -> logout(params);
                case "q", "quit" -> new ReplResult("quit", ReplResult.State.PRELOGIN);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.POSTLOGIN);
        }
    }

    public ReplResult list(String... params) throws ResponseException {
        responses.ListGamesResult games = server.listGames(authToken);
        StringBuilder outString = new StringBuilder();
        int cnt = 1;
        for (ListGameArrayResult game : games.games()) {
            outString.append(String.format("#: %d\tName: %s\tWhite: %s\tBlack: %s\n",
                    cnt, game.gameName(), printUser(game.whiteUsername()), printUser(game.blackUsername())));
            cnt++;
        }

        return new ReplResult(outString.toString(), ReplResult.State.POSTLOGIN);

    }

    public ReplResult create(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult res = server.createGame(authToken, new CreateGameRequest(params[0]));
            return new ReplResult(String.format("Game \"%s\" created.\n", params[0]), ReplResult.State.POSTLOGIN);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Game Name>");
    }

    //game ID here SHOULD BE INDEPENDENT
    public ReplResult join(String... params) throws ResponseException {
        try {
            int printedGameNum = Integer.parseInt(params[0]);
            if (params.length == 2 && printedGameNum >= 1) {
                ListGamesResult gamesList = server.listGames(authToken);
                int cnt = 1;
                int actualGameID = -1;
                for (ListGameArrayResult game : gamesList.games()) {
                    if (cnt == printedGameNum) {
                        actualGameID = game.gameID();
                        break;
                    } else {
                        cnt++;
                    }
                }
                if (actualGameID == -1) {
                    return new ReplResult("Invalid Game #", ReplResult.State.POSTLOGIN);
                }
                server.joinGame(authToken, new JoinGameRequest(params[1].toUpperCase(), actualGameID));
                joinedGameID = actualGameID;
                joinedColor = params[1].toUpperCase();
                return new ReplResult(String.format("Join Game #%s as %s.\n", params[0], params[1]), ReplResult.State.GAME);
            }
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: join <Game #> <WHITE/BLACK>");
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }


    }

    public ReplResult watch(String... params) throws ResponseException {
        try {
            int printedGameNum = Integer.parseInt(params[0]);
            if (params.length == 1 && printedGameNum >= 1) {
                ListGamesResult gamesList = server.listGames(authToken);
                int cnt = 1;
                int actualGameID = -1;
                for (ListGameArrayResult game : gamesList.games()) {
                    if (cnt == printedGameNum) {
                        actualGameID = game.gameID();
                        break;
                    } else {
                        cnt++;
                    }
                }
                joinedColor = null;
                joinedGameID = actualGameID;
                return new ReplResult(String.format("observing Game # %s.\n", actualGameID), ReplResult.State.GAME);
            }
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <Game #>");
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        }
    }

    public ReplResult logout(String... params) throws ResponseException {
        try {
            server.logout(authToken);
            return new ReplResult("Successfully logged out\n", ReplResult.State.PRELOGIN);
        } catch (ResponseException e) {
            return new ReplResult(e.getMessage(), ReplResult.State.POSTLOGIN);
        }

    }

    public ReplResult help() {
        return new ReplResult("""
                Options
                g - List current games
                c <Game Name> - Create a new game
                j <Game #> <Team Color> - Join an existing game
                w <Game #> - Watch a game
                h - Print this Help message again
                logout - Logout out of chess
                """, ReplResult.State.POSTLOGIN);
    }

    public int getJoinedGameID() {
        return joinedGameID;
    }

    public String getJoinedColor() {
        return joinedColor;
    }

    private String printUser(String username) {
        return (username == null) ? "" : username;
    }
}


