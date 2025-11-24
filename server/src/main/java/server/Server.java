package server;

import com.google.gson.Gson;
import dataaccess.*;
import handlers.*;
import server.websocket.WebSocketHandler;
import service.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        //setup Gson
        Gson json = new Gson();
        //Setup DAOs
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();
        //Setup Services
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGameService listGamesService = new ListGameService(authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);

        //Setup Handlers
        ClearHandler clearHandler = new ClearHandler(clearService, json);
        RegisterHandler registerHandler = new RegisterHandler(registerService, json);
        LoginHandler loginHandler = new LoginHandler(loginService, json);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService, json);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService, json);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService, json);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService, json);

        WebSocketHandler webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", clearHandler::handleRequest)
                .post("/user", registerHandler::handleRequest)
                .post("/session", loginHandler::handleRequest)
                .delete("session", logoutHandler::handleRequest)
                .get("/game", listGamesHandler::handleRequest)
                .post("/game", createGameHandler::handleRequest)
                .put("/game", joinGameHandler::handleRequest)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
