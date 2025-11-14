package ui;

import exception.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class GameClient {
    private String authToken;
    int joinedGame;
    private final ServerFacade server;
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;

    public GameClient(ServerFacade mainServer, PreLoginClient preClient, PostLoginClient postClient) throws ResponseException {
        server = mainServer;
        this.preClient = preClient;
        this.postClient = postClient;
    }


    public ReplResult eval(String input) {
        try {
            authToken = preClient.getAuthToken();
            joinedGame = postClient.getJoinedGameID();
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "t" -> throw new ResponseException(ResponseException.Code.ClientError, "");
                case "q", "quit" -> new ReplResult("quit\n", ReplResult.State.POSTLOGIN);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.GAME);
        }
    }


    public ReplResult help() {
        return new ReplResult("""
                Options
                idk
                """, ReplResult.State.GAME);
    }
}


