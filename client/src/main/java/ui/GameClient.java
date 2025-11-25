package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.Repl;
import exception.ResponseException;
import serverfacade.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GameClient implements NotificationHandler {
    private String authToken;
    private int joinedGame;
    private String joinedColor;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;

    public GameClient(ServerFacade mainServer, PreLoginClient preClient, PostLoginClient postClient, String serverUrl)
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
                case "b", "board", "r", "redraw", "newtab" -> redraw();
                case "leave" -> leave();
                case "m", "move" -> move(params);
                case "resign" -> resign();
                case "hl", "highlight" -> highlight(params);
                case "h", "help" -> help();
                case "q", "quit" -> new ReplResult("quit\n", ReplResult.State.POSTLOGIN);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ReplResult(ex.getMessage(), ReplResult.State.GAME);
        }
    }

    private ReplResult highlight(String... params) {
    }

    private ReplResult resign() throws ResponseException {
        ws.resignWSF(authToken, joinedGame);
        return new ReplResult("You resigned", ReplResult.State.GAME);
    }

    private ReplResult move(String... params) throws ResponseException {
        if (params.length >= 2) {
            ChessPosition start = translateChessPosition(params[0]);
            ChessPosition end = translateChessPosition(params[1]);
            ChessPiece.PieceType promotionPiece = null;
            if (params.length >= 3) {
                promotionPiece = switch (params[2].toLowerCase()) {
                    case "q", "queen" -> ChessPiece.PieceType.QUEEN;
                    case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
                    case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
                    case "r", "rook" -> ChessPiece.PieceType.ROOK;
                    default -> null;
                };
                return new ReplResult("Unknown Promotion Piece Type (queen, bishop, knight, rook)\n",
                        ReplResult.State.GAME);
            }
            ws.makeMoveWSF(authToken, joinedGame, new ChessMove(start, end, promotionPiece));
        } else {
            return new ReplResult("""
                        Expected m <square> <square> <OPTIONAL: promotion piece>
                        ex: m a2 a4
                    press q to leave game
                    """, ReplResult.State.GAME);
        }

        return null;
    }

    private ChessPosition translateChessPosition(String param) {
        return new ChessPosition(param.charAt(0), param.charAt(1));
    }

    private ReplResult leave() throws ResponseException {
        ws.leaveGameWSF(authToken, joinedGame);
        return new ReplResult("You left\n", ReplResult.State.POSTLOGIN);
    }


    public ReplResult redraw() {
        ChessBoard blankBoard = (new ChessBoard());
        blankBoard.resetBoard();
        return new ReplResult(DrawBoard.draw(joinedColor, blankBoard), ReplResult.State.GAME);
    }

    public ReplResult help() {
        return new ReplResult("""
                Expected m <StartSquare> <EndSquare> <Optional: Promotion Piece> 
                    ex: m a2 a4
                press q to leave game
                """, ReplResult.State.GAME);
    }


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGameData());
        }
    }

    private void displayNotification(String message) {

    }

    private void displayError(String errorMsg) {

    }

    private void loadGame(GameData game) {

    }
}


