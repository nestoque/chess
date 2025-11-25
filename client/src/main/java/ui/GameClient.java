package ui;

import chess.ChessBoard;
import exception.ResponseException;
import serverfacade.ServerFacade;
import ui.websocket.WebSocketFacade;
import websocket.messages.LoadGameMessage;

import java.util.Arrays;

public class GameClient {
    private String authToken;
    private int joinedGame;
    private String joinedColor;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;

    public GameClient(ServerFacade mainServer, PreLoginClient preClient, PostLoginClient postClient)
            throws ResponseException {
        server = mainServer;
        this.preClient = preClient;
        this.postClient = postClient;
        ws = new WebSocketFacade(serverUrl, this);
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


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

}


