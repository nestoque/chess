package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.ResponseException;

import jakarta.websocket.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectWSF(String authToken, int gameID, String team) throws ResponseException {
        var action = new ConnectCommand(authToken, gameID, team);
        sendCommand(action);
    }

    public void leaveGameWSF(String authToken, int gameID) throws ResponseException {
        var action = new LeaveGameCommand(authToken, gameID);
        sendCommand(action);
    }

    public void resignWSF(String authToken, int gameID) throws ResponseException {
        var action = new ResignCommand(authToken, gameID);
        sendCommand(action);

    }

    public void makeMoveWSF(String authToken, int gameID, ChessMove move) throws ResponseException {
        var action = new MakeMoveCommand(authToken, gameID, move);
        sendCommand(action);
    }

    private void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            String json = new Gson().toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private void handleMessage(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case NOTIFICATION -> {
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);
                }
                case ERROR -> {
                    ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                    notificationHandler.notify(error);
                }
                case LOAD_GAME -> {
                    LoadGameMessage load = new Gson().fromJson(message, LoadGameMessage.class);
                    notificationHandler.notify(load);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
    }


}