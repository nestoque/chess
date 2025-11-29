package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;

import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
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
                    // 1. Deserialize to the parent class just to get the type
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    // 2. Switch on the type and re-deserialize to the specific child class
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
        try {
            var action = new ConnectCommand(authToken, gameID, team);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leaveGameWSF(String authToken, int gameID) throws ResponseException {
        try {
            var action = new LeaveGameCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resignWSF(String authToken, int gameID) throws ResponseException {
        try {
            var action = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMoveWSF(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var action = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private void handleMessage(String messageString) {
        try {
            ServerMessage message = (new Gson()).fromJson(messageString, ServerMessage.class);
            this.notificationHandler.notify(message);
        } catch (Exception ex) {
            this.notificationHandler.notify(new ErrorMessage(ex.getMessage()));
        }
    }


}