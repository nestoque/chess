package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.Repl;
import exception.ResponseException;
import responses.ListGameArrayResult;
import responses.ListGamesResult;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class GameClient {
    private String authToken;
    private int joinedGame;
    private String joinedColor;
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
            joinedColor = postClient.getJoinedColor();
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "t" -> throw new ResponseException(ResponseException.Code.ClientError, "");
                case "b", "board" -> help();
                case "h", "help" -> help();
                case "q", "quit" -> new ReplResult("quit\n", ReplResult.State.POSTLOGIN);
                default -> realHelp();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.GAME);
        }
    }


    public ReplResult help() {
        ChessBoard blankBoard = (new ChessBoard());
        blankBoard.resetBoard();
        return new ReplResult(DrawBoard.draw(joinedColor, blankBoard), ReplResult.State.GAME);
    }

    public ReplResult realHelp() {
        return new ReplResult("""
                Expected <square> <square> 
                    ex: a2 a4
                press q to leave game
                """, ReplResult.State.GAME);
    }
//    ListGamesResult res = server.listGames(authToken);
//        for (ListGameArrayResult game: res.games()) {
//            if (game.gameID() == joinedGame){
//                return DrawBoard.draw(game.)
//            }
//        }
//    }
}


