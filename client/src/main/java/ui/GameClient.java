package ui;

import chess.*;
import exception.ResponseException;
import serverfacade.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;

public class GameClient implements NotificationHandler {
    private String authToken;
    private int joinedGame;
    private String joinedColor;
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;
    private final String serverUrl;

    public GameClient(ServerFacade mainServer, PreLoginClient preClient, PostLoginClient postClient, String newServerUrl)
            throws ResponseException {
        server = mainServer;
        this.preClient = preClient;
        this.postClient = postClient;
        serverUrl = newServerUrl;
    }


    public ReplResult eval(String input) {
        try {
            try {
                ws.connectWSF(authToken, joinedGame, joinedColor);
            } catch (ResponseException e) {
                return new ReplResult("Failed to connect to game", ReplResult.State.POSTLOGIN);
            }
            ws = new WebSocketFacade(serverUrl, this);
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
        if (params.length >= 1) {
            ChessPosition posForMoves = translateChessPosition(params[0]);
            ChessGame myGame = new ChessGame();// GET GAME
            myGame.getBoard().resetBoard();
            return new ReplResult(DrawBoard.draw(joinedColor, myGame.getBoard(), myGame.validMoves(posForMoves)), ReplResult.State.GAME);
        } else {
            return new ReplResult("""
                        Expected hl <square> 
                        ex: hl a2
                    """, ReplResult.State.GAME);
        }
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
                if (promotionPiece == null) {
                    return new ReplResult("Unknown Promotion Piece Type (queen, bishop, knight, rook)\n",
                            ReplResult.State.GAME);
                }
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
        char colChar = param.charAt(0); // 'a'
        char rowChar = param.charAt(1); // '2'
        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);
        return new ChessPosition(row, col);
    }

    private ReplResult leave() throws ResponseException {
        ws.leaveGameWSF(authToken, joinedGame);
        return new ReplResult("You left\n", ReplResult.State.POSTLOGIN);
    }


    public ReplResult redraw() {
        ChessBoard blankBoard = (new ChessBoard());
        blankBoard.resetBoard();
        return new ReplResult(DrawBoard.draw(joinedColor, blankBoard, null), ReplResult.State.GAME);
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


