package java.client;

import chess.ChessGame;
import exception.ResponseException;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.junit.jupiter.api.*;
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


public class ServerFacadeTests {

    private String username = "test_user";
    private String password = "test_password";
    private String encryptedPass = BCrypt.hashpw("test_password", BCrypt.gensalt());
    private String email = "fake_email@gmail.com";
    private String gameName = "Test_Game";
    private String playerColor = "WHITE";


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

    @Test
    public void registerSuccess() throws ResponseException {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

    //Clear Positive
    @Test
    @Order(1)
    @DisplayName("Clear +")
    public void cleanup() {
        UserData testUser = new UserData(username, encryptedPass, email);
        userDAO.addUser(testUser);
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);
        GameData testGame = new GameData(1, null, null, gameName, new ChessGame());
        gameDAO.addGame(testGame);
        clearService.clear();
        assertNull(userDAO.getUser(username), "username not cleared");
        assertNull(authDAO.getAuth(testToken), "auth not cleared");
        assertNull(gameDAO.getGame(1), "games not cleared");
    }

    @Test
    @Order(2)
    @DisplayName("Clear Service Init")
    public void clearServiceSetup() {
        assertEquals(userDAO, clearService.userDAO);
        assertEquals(authDAO, clearService.authDAO);
        assertEquals(gameDAO, clearService.gameDAO);
    }

    //Register +
    @Test
    @Order(3)
    @DisplayName("Register +")
    public void registerSuccess() throws ServiceException {
        RegisterRequest newRegReq = new RegisterRequest(username, password, email);
        UserData expectedUserData = new UserData(username, encryptedPass, email);
        RegisterResult newRegRes = registerService.register(newRegReq);
        UserData actualUser = userDAO.getUser(username);

        Assertions.assertNotNull(actualUser, "User not found after Registration");
        Assertions.assertEquals(username, actualUser.username(), "Username did not match");
        Assertions.assertEquals(email, actualUser.email(), "Email did not match");
        Assertions.assertTrue(
                BCrypt.checkpw(password, actualUser.password()),
                "Password was not hashed correctly"
        );

        AuthData actualAuthData = authDAO.getAuth(newRegRes.authToken());
        Assertions.assertNotNull(actualAuthData, "User not logged in after Registration");
    }

    //Register -
    @Test
    @Order(4)
    @DisplayName("Register -")
    public void registerAlreadyTakenFail() throws ServiceException {
        UserData testUser = new UserData(username, encryptedPass, email);
        userDAO.addUser(testUser);

        RegisterRequest dupeRegReq = new RegisterRequest(username, password, email);
        ServiceException exception = assertThrows(ServiceException.class, () -> registerService.register(dupeRegReq), "Didn't throw exception");

        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    //Login +
    @Test
    @Order(5)
    @DisplayName("Login +")
    public void loginSuccess() throws ServiceException {
        UserData testUser = new UserData(username, encryptedPass, email);
        userDAO.addUser(testUser);

        LoginRequest newLoginReq = new LoginRequest(username, password);
        LoginResult newLoginRes = loginService.login(newLoginReq);
        AuthData actualAuthData = authDAO.getAuth(newLoginRes.authToken());
        Assertions.assertEquals(username, newLoginRes.username(), "Username not correctly logged in");
        Assertions.assertNotNull(actualAuthData, "User not logged in");
    }

    //Login -
    @Test
    @Order(6)
    @DisplayName("Login -")
    public void loginWrongPassword() throws ServiceException {
        UserData testUser = new UserData(username, BCrypt.hashpw("different password", BCrypt.gensalt()), email);
        userDAO.addUser(testUser);

        LoginRequest newLoginReq = new LoginRequest(username, password);
        ServiceException exception = assertThrows(ServiceException.class, () -> loginService.login(newLoginReq), "Didn't throw exception");
        assertEquals(401, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }

    //Logout +
    @Test
    @Order(7)
    @DisplayName("Logout +")
    public void logoutSucces() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        logoutService.logout(testToken);
        Assertions.assertNull(authDAO.getAuth(testToken), "Not successfully logged out");
    }

    //Logout -
    @Test
    @Order(8)
    @DisplayName("Logout -")
    public void logoutWrongAuth() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        String badAuthToken = TokenUtils.generateToken();
        ServiceException exception = assertThrows(ServiceException.class, () -> logoutService.logout(badAuthToken), "Didn't throw exception");
        assertEquals(401, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }

    //Create Game +
    @Test
    @Order(9)
    @DisplayName("Create Game +")
    public void createGameSuccess() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        CreateGameRequest newCreateGameReq = new CreateGameRequest(gameName);
        CreateGameResult expectedCreateGameRes = new CreateGameResult(1);
        assertEquals(createGameService.createGame(testToken, newCreateGameReq), expectedCreateGameRes);
    }

    //Create Game -
    @Test
    @Order(10)
    @DisplayName("Create Game -")
    public void createGameNoName() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        CreateGameRequest badCreateGameReq = new CreateGameRequest("");
        ServiceException exception = assertThrows(ServiceException.class, () ->
                createGameService.createGame(testToken, badCreateGameReq), "Didn't throw exception");
        assertEquals(400, exception.getStatusCode());
        assertEquals("bad request", exception.getMessage());
    }

    //Join Game +
    @Test
    @Order(11)
    @DisplayName("Join Game +")
    public void joinGameSuccess() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        GameData testGame = new GameData(1, null, null, gameName, new ChessGame());
        gameDAO.addGame(testGame);

        JoinGameRequest newJoinGameReq = new JoinGameRequest(playerColor, 1);
        joinGameService.joinGame(testToken, newJoinGameReq);

        assertEquals(gameDAO.getGame(1).whiteUsername(), username, "User unable to join game");

    }

    //Join Game -
    @Test
    @Order(12)
    @DisplayName("Join Game -")
    public void joinGameSameColor() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        GameData testGame = new GameData(1, "player1", null, gameName, new ChessGame());
        gameDAO.addGame(testGame);

        JoinGameRequest newJoinGameReq = new JoinGameRequest(playerColor, 1);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                joinGameService.joinGame(testAuth.authToken(), newJoinGameReq), "Didn't throw exception");
        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    //List Game +
    @Test
    @Order(13)
    @DisplayName("List Game +")
    public void listGameSuccess() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        CreateGameRequest newCreateGameReq = new CreateGameRequest(gameName);
        createGameService.createGame(testToken, newCreateGameReq);
        createGameService.createGame(testToken, newCreateGameReq);
        createGameService.createGame(testToken, newCreateGameReq);
        createGameService.createGame(testToken, newCreateGameReq);


        ListGamesResult actualListGamesResult = listGamesService.listGames(testToken);
        int num = 1;
        for (ListGameArrayResult entry : actualListGamesResult.games()) {
            assertEquals(entry.gameID(), num++, "Missing Game");
        }

    }

    //List Game -
    @Test
    @Order(14)
    @DisplayName("List Game -")
    public void listGameUnauthorized() throws ServiceException {
        String badAuthToken = TokenUtils.generateToken();

        ServiceException exception = assertThrows(ServiceException.class, () -> listGamesService.listGames(badAuthToken), "Didn't throw exception");
        assertEquals(401, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }
}

}
