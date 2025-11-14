package client;

import chess.ChessGame;
import exception.ResponseException;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import server.Server;
import serverfacade.ServerFacade;
import service.ServiceException;
import utils.TokenUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private final String USERNAME = "test_user";
    private final String PASSWORD = "test_password";
    private final String ENCRYPTEDPASS = BCrypt.hashpw("test_password", BCrypt.gensalt());
    private final String EMAIL = "fake_email@gmail.com";
    private final String GAME_NAME = "Test_Game";
    private final String PLAYER_COLOR = "WHITE";

    private final CreateGameRequest CREATE_GAME_REQ = new CreateGameRequest(GAME_NAME);
    private final JoinGameRequest JOIN_GAME_REQ = new JoinGameRequest(PLAYER_COLOR, 1);
    private final LoginRequest LOGIN_REQ = new LoginRequest(USERNAME, PASSWORD);
    private final RegisterRequest REGISTER_REQ = new RegisterRequest(USERNAME, PASSWORD, EMAIL);

    private final CreateGameResult CREATE_GAME_RES = new CreateGameResult(1);
    //private final ListGamesResult LIST_GAME_RES = new ListGamesResult();
    //private final LoginResult LOGIN_RES = new LoginResult();
    //private final RegisterResult REGISTER_RES = new RegisterResult() ;

    private static Server server;
    private static ServerFacade facade;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    //Clear Positive
    @Test
    @Order(1)
    @DisplayName("Clear +")
    public void cleanup() throws ResponseException {
        assertDoesNotThrow(() -> facade.clear(), "Throws unexpected error");
    }

    @Test
    @Order(2)
    @DisplayName("Clear -")
    public void clearCheck() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        CreateGameResult gameRes = facade.createGame(res.authToken(), CREATE_GAME_REQ);

        facade.clear();
//        \assertThrows(ResponseException.class, () -> facade.logout(res.authToken()), "Didn't throw exception"); //401 unauthorized
        assertThrows(ResponseException.class, () -> facade.login(LOGIN_REQ), "Didn't throw exception");
    }

    //Register +
    @Test
    @Order(3)
    @DisplayName("Register +")
    public void registerSuccess() throws ResponseException {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        var RegisterResponse = facade.register(req);
        assertTrue(RegisterResponse.authToken().length() > 10);
    }

    //Register -
    @Test
    @Order(4)
    @DisplayName("Register -")
    public void registerAlreadyTakenFail() throws ResponseException {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        var RegisterResponse = facade.register(req);
        ResponseException exception = assertThrows(ResponseException.class, () -> facade.register(req), "Didn't throw exception");

        assertEquals(ResponseException.Code.ClientError, exception.code());
        assertEquals("Error: already taken", exception.getMessage());
    }

    //Login +
    @Test
    @Order(5)
    @DisplayName("Login +")
    public void loginSuccess() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        facade.logout(res.authToken());
        LoginResult LogRes = facade.login(LOGIN_REQ);
        assertNotNull(LogRes, "Invalid Login");
        Assertions.assertEquals(USERNAME, LogRes.username(), "Username not correctly logged in");
    }

    //Login -
    @Test
    @Order(6)
    @DisplayName("Login -")
    public void loginWrongPassword() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        facade.logout(res.authToken());
        ResponseException exception = assertThrows(ResponseException.class, () -> facade.login(new LoginRequest(USERNAME, "different password")), "Didn't throw exception");
        assertEquals(ResponseException.Code.ClientError, exception.code());
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    //Logout +
    @Test
    @Order(7)
    @DisplayName("Logout +")
    public void logoutSuccess() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        assertDoesNotThrow(() -> facade.logout(res.authToken()), "throws error");
        assertThrows(ResponseException.class, () -> facade.createGame(res.authToken(), CREATE_GAME_REQ), "Didn't throw exception");
    }

    //Logout -
    @Test
    @Order(8)
    @DisplayName("Logout -")
    public void logoutWrongAuth() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        String otherAuthToken = TokenUtils.generateToken();
        assertThrows(ResponseException.class, () -> facade.logout(otherAuthToken), "Didn't throw exception for incorrect auth token");
    }

    //Create Game +
    @Test
    @Order(9)
    @DisplayName("Create Game +")
    public void createGameSuccess() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        CreateGameResult gameRes = facade.createGame(res.authToken(), CREATE_GAME_REQ);
        assertEquals(gameRes, new CreateGameResult(1));

    }

    //Create Game -
    @Test
    @Order(10)
    @DisplayName("Create Game -")
    public void createGameNoName() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        ResponseException exception = assertThrows(ResponseException.class, () ->
                facade.createGame(res.authToken(), new CreateGameRequest(null)), "Didn't throw exception");
        assertEquals(ResponseException.Code.ClientError, exception.code());
        assertEquals("bad request", exception.getMessage());
    }

    //Join Game +
    @Test
    @Order(11)
    @DisplayName("Join Game +")
    public void joinGameSuccess() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        CreateGameResult gameRes = facade.createGame(res.authToken(), CREATE_GAME_REQ);
        assertDoesNotThrow(() -> facade.joinGame(res.authToken(), JOIN_GAME_REQ), "threw error while joining game");
    }

    //Join Game -
    @Test
    @Order(12)
    @DisplayName("Join Game -")
    public void joinGameFakeGame() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);
        CreateGameResult gameRes = facade.createGame(res.authToken(), CREATE_GAME_REQ);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                facade.joinGame(res.authToken(), new JoinGameRequest(PLAYER_COLOR, gameRes.gameID() + 1)), "Didn't throw exception");
        assertEquals(400, exception.code());
        assertEquals("bad request", exception.getMessage());
    }

    //List Game +
    @Test
    @Order(13)
    @DisplayName("List Game +")
    public void listGameSuccess() throws ResponseException {
        RegisterResult res = facade.register(REGISTER_REQ);

        facade.createGame(res.authToken(), CREATE_GAME_REQ);
        facade.createGame(res.authToken(), CREATE_GAME_REQ);
        facade.createGame(res.authToken(), CREATE_GAME_REQ);
        facade.createGame(res.authToken(), CREATE_GAME_REQ);


        ListGamesResult actualListGamesResult = facade.listGames(res.authToken());
        int num = 1;
        for (ListGameArrayResult entry : actualListGamesResult.games()) {
            assertEquals(entry.gameID(), num++, "Missing Game");
        }

    }

    //List Game -
    @Test
    @Order(14)
    @DisplayName("List Game -")
    public void listGameUnauthorized() throws ResponseException {
        String badAuthToken = TokenUtils.generateToken();

        ServiceException exception = assertThrows(ServiceException.class, () -> facade.listGames(badAuthToken), "Didn't throw exception");
        assertEquals(400, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }


}
