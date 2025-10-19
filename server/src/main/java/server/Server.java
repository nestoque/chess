package server;

import handlers.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", (new ClearHandler()).handleRequest())
                .post("/user", (new RegisterHandler().handleRequest()))
                .post("/session", (new LoginHandler().handleRequest()))
                .delete("session", (new LogoutHandler().handleRequest()))
                .get("/game", (new ListGamesHandler()).handleRequest())
                .post("/game", (new CreateGameHandler()).handlRequest())
                .put("/game", (new JoinGameHandler().handleRequest()));

        // Register your endpoints and exception handlers here.
        //Registration

        //Login

        //Logout

        //List Games

        //Create Game

        //Join Game

        //Clear application


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
