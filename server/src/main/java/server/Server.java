package server;

import dataaccess.*;
import handlers.*;
import services.*;
import io.javalin.*;
import org.eclipse.jetty.util.log.Log;

public class Server {

    private final Javalin javalin;

    public Server() {
        //Setup DAOs
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        //Setup Services
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGameService listGamesService = new ListGameService(authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);

        //Setup Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        LoginHandler loginHandler = new LoginHandler(loginService);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", clearHandler::handleRequest)
                .post("/user", registerHandler::handleRequest)
                .post("/session", loginHandler::handleRequest)
                .delete("session", logoutHandler::handleRequest)
                .get("/game", listGamesHandler::handleRequest)
                .post("/game", createGameHandler::handleRequest)
                .put("/game", joinGameHandler::handleRequest);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
