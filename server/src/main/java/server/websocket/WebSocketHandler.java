package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import object.AuthData;
import object.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

import static javax.management.remote.JMXConnectorFactory.connect;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        Gson gson = new Gson();

        try {
            UserGameCommand command = gson.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            connections.add(session, gameId);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> {
                    MakeMoveCommand cmd = gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(session, username, cmd);
                } //double deseriealizaTION
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (ServiceException ex) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void sendMessage(Session session, ErrorMessage errorMessage) throws IOException {
        session.getRemote().sendString(errorMessage.getErrorMessage());
    }

    private String getUsername(String authToken) {
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData thisAuth = authDAO.getAuth(authToken);
        return thisAuth.username();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    //connect
    private void connect(int gameID, String authToken, String team, Session session) throws IOException {
        //Send load game back
        GameDAO gameDAO = new SQLGameDAO();
        GameData gameData = gameDAO.getGame(gameID);
        var notifyLoadGame = new LoadGameMessage(gameData);
        session.getRemote().sendString(notifyLoadGame.toString());
        //Send notification connect to all
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData thisAuth = authDAO.getAuth(authToken);
        if (team == null) {
            team = "observer";
        }
        var message = String.format("%s has joined as %s", thisAuth.username(), team);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    //make move
    public void makeMove(String petName, String username, String sound) throws ResponseException {
        //verify move
        //Game is updated to represent the move. Game is updated in the database.
        //Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
        //Server sends a Notification message to all other clients in that game informing them what move was made.
        //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.


        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new LoadGameMessage(message);
            connections.broadcast(null, notification);

            GameDAO gameDAO = new SQLGameDAO();
            GameData gameData = gameDAO.getGame(gameID);
            var notifyLoadGame = new LoadGameMessage(gameData);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //leave
    private void leaveGame(String username, Session session) throws IOException {
        //If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
        //Server sends a Notification message to all other clients in that game informing them that the root client left.
        //This applies to both players and observers.
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        connections.remove(session);
    }

    //resign
    private void resign(String visitorName, Session session) throws IOException {
        //Server marks the game as over (no more moves can be made). Game is updated in the database.
        //Server sends a Notification message to all clients in that game informing them that the root client resigned.
        // This applies to both players and observers.
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }


}