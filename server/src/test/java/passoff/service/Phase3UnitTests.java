package passoff.service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import handlers.*;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import services.*;
import utils.TokenUtils;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phase3UnitTests {

    private String username = "test_user";
    private String password = "test_password";
    private String email = "fake_email@gmail.com";
    private String gameName = "Test_Game";
    private String playerColor = "WHITE";

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGameService listGamesService;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;


    @BeforeEach
    public void startup() {
        //Setup DAOs
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        //Setup Services
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        listGamesService = new ListGameService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);
    }

    @AfterEach
    public void clear() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
    }
    // ### SERVICE UNIT TESTS ###

    //Clear Positive
    @Test
    @DisplayName("Clear +")
    public void cleanup() {
        UserData testUser = new UserData(username, password, email);
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


    //Register +
    @Test
    @DisplayName("Register +")
    public void registerSuccess() throws ServiceException {
        RegisterRequest newRegReq = new RegisterRequest(username, password, email);
        UserData expectedUserData = new UserData(username, password, email);
        RegisterResult newRegRes = registerService.register(newRegReq);
        Assertions.assertEquals(expectedUserData, userDAO.getUser(username), "User not found after Registration");

        AuthData actualAuthData = authDAO.getAuth(newRegRes.authToken());
        Assertions.assertNotNull(actualAuthData, "User not logged in after Registration");
    }

    //Register -
    @Test
    @DisplayName("Register -")
    public void registerAlreadyTakenFail() throws ServiceException {
        UserData testUser = new UserData(username, password, email);
        userDAO.addUser(testUser);

        RegisterRequest dupeRegReq = new RegisterRequest(username, password, email);
        ServiceException exception = assertThrows(ServiceException.class, () -> registerService.register(dupeRegReq), "Didn't throw exception");

        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    //Login +
    @Test
    @DisplayName("Login +")
    public void loginSuccess() throws ServiceException {
        UserData testUser = new UserData(username, password, email);
        userDAO.addUser(testUser);

        LoginRequest newLoginReq = new LoginRequest(username, password);
        LoginResult newLoginRes = loginService.login(newLoginReq);
        AuthData actualAuthData = authDAO.getAuth(newLoginRes.authToken());
        Assertions.assertEquals(username, newLoginRes.username(), "Username not correctly logged in");
        Assertions.assertNotNull(actualAuthData, "User not logged in");
    }

    //Login -
    @Test
    @DisplayName("Login -")
    public void loginWrongPassword() throws ServiceException {
        UserData testUser = new UserData(username, "different password", email);
        userDAO.addUser(testUser);

        LoginRequest newLoginReq = new LoginRequest(username, email);
        ServiceException exception = assertThrows(ServiceException.class, () -> loginService.login(newLoginReq), "Didn't throw exception");
        assertEquals(401, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }

    //Logout +
    @Test
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
    @DisplayName("Create Game -")
    public void createGameNoName() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        CreateGameRequest badCreateGameReq = new CreateGameRequest("");
        ServiceException exception = assertThrows(ServiceException.class, () -> createGameService.createGame(testToken, badCreateGameReq), "Didn't throw exception");
        assertEquals(400, exception.getStatusCode());
        assertEquals("bad request", exception.getMessage());
    }

    //Join Game +
    @Test
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
    @DisplayName("Join Game -")
    public void joinGameSameColor() throws ServiceException {
        String testToken = TokenUtils.generateToken();
        AuthData testAuth = new AuthData(testToken, username);
        authDAO.addAuth(testAuth);

        GameData testGame = new GameData(1, "player1", null, gameName, new ChessGame());
        gameDAO.addGame(testGame);

        JoinGameRequest newJoinGameReq = new JoinGameRequest(playerColor, 1);

        ServiceException exception = assertThrows(ServiceException.class, () -> joinGameService.joinGame(testAuth.authToken(), newJoinGameReq), "Didn't throw exception");
        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    //List Game +
    @Test
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
    @DisplayName("List Game -")
    public void listGameUnauthorized() throws ServiceException {
        String badAuthToken = TokenUtils.generateToken();

        ServiceException exception = assertThrows(ServiceException.class, () -> listGamesService.listGames(badAuthToken), "Didn't throw exception");
        assertEquals(401, exception.getStatusCode());
        assertEquals("unauthorized", exception.getMessage());
    }
}