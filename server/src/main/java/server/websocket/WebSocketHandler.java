package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import io.javalin.websocket.*;
import object.AuthData;
import object.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private static final GameDAO gameDAO = new SQLGameDAO();
    private static final UserDAO userDAO = new SQLUserDAO();
    private static final AuthDAO authDAO = new SQLAuthDAO();
    private static final Gson gson = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = gson.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            connections.add(session, gameId);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand cmd = gson.fromJson(wsMessageContext.message(), ConnectCommand.class);
                    connect(session, username, cmd);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand cmd = gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(session, username, cmd);
                } //double deseriealizaTION
                case LEAVE -> {
                    LeaveGameCommand cmd = gson.fromJson(wsMessageContext.message(), LeaveGameCommand.class);
                    leaveGame(session, username, cmd);
                }
                case RESIGN -> {
                    ResignCommand cmd = gson.fromJson(wsMessageContext.message(), ResignCommand.class);
                    resign(session, username, (ResignCommand) command);
                }
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void sendMessage(Session session, ErrorMessage errorMessage) throws IOException {
        session.getRemote().sendString(errorMessage.toString());
    }

    private String getUsername(String authToken) throws UnauthorizedException {
        AuthData thisAuth = authDAO.getAuth(authToken);
        if (thisAuth == null) {
            throw new UnauthorizedException();
        }
        return thisAuth.username();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    //connect
    private void connect(Session session, String username, ConnectCommand cmd) throws IOException {
        //Send load game back
        GameData gameData = gameDAO.getGame(cmd.getGameID());
        if (gameData == null) {
            session.getRemote().sendString((new ErrorMessage("Invalid Game")).toString());
            return;
        }
        var notifyLoadGame = new LoadGameMessage(gameData);
        session.getRemote().sendString(notifyLoadGame.toString());
        //Send notification connect to all
        String team = cmd.getTeam();
        if (team == null) {
            team = "observer";
        }
        var message = String.format("%s has joined as %s", username, team);
        var notification = new NotificationMessage(message);
        connections.broadcast(cmd.getGameID(), session, notification);
    }

    //make move
    public void makeMove(Session session, String username, MakeMoveCommand cmd) throws ResponseException, IOException {


        try {
            //verify move
            //Game is updated to represent the move. Game is updated in the database.
            GameData gameData = gameDAO.getGame(cmd.getGameID());
            ChessGame.TeamColor checkCheck =
                    (gameData.whiteUsername().equals(username)) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            if (gameData.game().isEndGame()) {
                var notification = new ErrorMessage("Unable to move: Game already over");
                sendMessage(session, notification);
                return;
            }
            if (checkCheck.equals(gameData.game().getBoard().getPiece(cmd.getMove().getStartPosition()).getTeamColor())) {
                var notification = new ErrorMessage("Unable to move: not your turn");
                sendMessage(session, notification);
                return;
            }

            gameData.game().makeMove(cmd.getMove());
            gameDAO.updateGame(gameData);
            //Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
            var notifyLoadGame = new LoadGameMessage(gameData);
            connections.broadcastAllInGame(cmd.getGameID(), notifyLoadGame);
            //Server sends a Notification message to all other clients in that game informing them what move was made.
            var message = String.format("%s moved %s", username, cmd.getMove().toString());
            var notification = new NotificationMessage(message);
            connections.broadcast(cmd.getGameID(), session, notification);
            //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
            if (gameData.game().isInCheckmate(checkCheck)) {
                var checkMessage = String.format("%s checkmated %s", username, cmd.getMove().toString());
                var checkNotification = new NotificationMessage(checkMessage);
                connections.broadcastAllInGame(cmd.getGameID(), checkNotification);
                //mark game as done
            } else if (gameData.game().isInStalemate(checkCheck)) {
                var checkMessage = String.format("%s is in stalemate", username, cmd.getMove().toString());
                var checkNotification = new NotificationMessage(checkMessage);
                connections.broadcastAllInGame(cmd.getGameID(), checkNotification);
                //mark game as done
            }

        } catch (InvalidMoveException ex) {
            sendMessage(session, new ErrorMessage("Invalid Move"));
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //leave
    private void leaveGame(Session session, String username, LeaveGameCommand cmd) throws IOException {
        //If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
        //Server sends a Notification message to all other clients in that game informing them that the root client left.
        //This applies to both players and observers.
        var message = String.format("%s left the game", username);
        GameData gameData = gameDAO.getGame(cmd.getGameID());
        if (gameData.whiteUsername().equals(username)) {
            gameData = gameData.setWhiteUsername(null);
            gameDAO.updateGame(gameData);
        } else if (gameData.blackUsername().equals(username)) {
            gameData = gameData.setBlackUsername(null);
            gameDAO.updateGame(gameData);
        }
        var notification = new NotificationMessage(message);
        connections.broadcast(cmd.getGameID(), session, notification);
        connections.remove(session);
    }

    //resign
    private void resign(Session session, String username, ResignCommand cmd) throws IOException {
        //Server marks the game as over (no more moves can be made). Game is updated in the database.
        //Server sends a Notification message to all clients in that game informing them that the root client resigned.
        // This applies to both players and observers.
        GameData gameData = gameDAO.getGame(cmd.getGameID());
        gameData.game().setEndGame();
        gameDAO.updateGame(gameData);
        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(message);
        connections.broadcastAllInGame(cmd.getGameID(), notification);
    }


}