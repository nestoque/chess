package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.eclipse.jetty.http.HttpTokens;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import responses.*;
import service.*;
import utils.TokenUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phase4UnitTests {

    final private String username = "test_user";
    final private String password = "test_password";
    final private String encryptedPass = BCrypt.hashpw("test_password", BCrypt.gensalt());
    final private String email = "fake_email@gmail.com";
    final private String gameName = "Test_Game";
    final private String playerColor = "WHITE";
    final private String testToken = TokenUtils.generateToken();

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void startup() {
        //Setup DAOs
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        //Setup Services
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @AfterEach
    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
    // ### SQL DATAACCESS UNIT TESTS ###

    @Test
    @Order(1)
    @DisplayName("AddAuth +")
    public void addAuthSuccess() {
        assertTrue(authDAO.addAuth(new AuthData(testToken, username)));
    }

    @Test
    @Order(2)
    @DisplayName("AddAuth -")
    public void addAuthFail() {
        AuthData testUser = new AuthData(testToken, username);
        authDAO.addAuth(testUser);
        assertFalse(authDAO.addAuth(new AuthData(testToken, username)), "addAuth didn't return false on repeated user");
    }


    @Test
    @Order(3)
    @DisplayName("getAuth +")
    public void getAuthSuccess() {
        authDAO.addAuth(new AuthData(testToken, username));
        assertNotNull(authDAO.getAuth(testToken));
    }


    @Test
    @Order(4)
    @DisplayName("getAuth -")
    public void getAuthFail() {
        authDAO.addAuth(new AuthData(testToken, username));
        assertNull(authDAO.getAuth("othertoken"));
    }


    @Test
    @Order(5)
    @DisplayName("deleteAuth +")
    public void deleteAuthSuccess() {
        authDAO.addAuth(new AuthData(testToken, username));
        authDAO.deleteAuth(testToken);
        assertNull(authDAO.getAuth(testToken));
    }


    @Test
    @Order(6)
    @DisplayName("deleteAuth -") //another idea is deleting the wrong one make sure the og one still there.
    public void deleteAuthFail() {
        authDAO.addAuth(new AuthData(testToken, username));
        String newToken = TokenUtils.generateToken();
        authDAO.deleteAuth(newToken);
        ;
        assertNotNull(authDAO.getAuth(testToken));
    }

    @Test
    @Order(7)
    @DisplayName("authClear")
    public void authClearSuccess() {
        authDAO.addAuth(new AuthData(testToken, username));
        authDAO.clear();
        assertNull(authDAO.getAuth(testToken));
    }


    @Test
    @Order(8)
    @DisplayName("addUser +")
    public void addUserSuccess() {
        assertTrue(userDAO.addUser(new UserData(username, password, email)));
    }


    @Test
    @Order(9)
    @DisplayName("addUser -")
    public void addUserFail() {
        UserData testUser = new UserData(username, password, email);
        userDAO.addUser(testUser);
        assertFalse(userDAO.addUser(testUser), "addAuth didn't return false on repeated user");
    }


    @Test
    @Order(10)
    @DisplayName("getUser +")
    public void getUserSuccess() {
        userDAO.addUser(new UserData(username, password, email));
        assertNotNull(userDAO.getUser(username));
    }


    @Test
    @Order(11)
    @DisplayName("getUser -")
    public void getUserFail() {
        userDAO.addUser(new UserData(username, password, email));
        assertNull(userDAO.getUser("otheruser"));
    }


    @Test
    @Order(12)
    @DisplayName("clearUser")
    public void clearUserSuccess() {
        userDAO.addUser(new UserData(username, password, email));
        userDAO.clear();
        assertNull(userDAO.getUser("otheruser"));
    }


    @Test
    @Order(13)
    @DisplayName("addGame +")
    public void addGameSuccess() {
        assertEquals(1, gameDAO.addGame(new GameData(1, null, null, gameName,
                new ChessGame())));
        assertEquals(2, gameDAO.addGame(new GameData(1, null, null, gameName,
                new ChessGame())));
    }


    @Test
    @Order(14)
    @DisplayName("addGame -")
    public void addGameFail() {
        //give not null a null
        assertThrows(
                RuntimeException.class, () -> gameDAO.addGame(new GameData(47, null, null,
                        null, new ChessGame())), "Didn't throw exception");
    }

    @Test
    @Order(15)
    @DisplayName("getGame +")
    public void getGameSuccess() {
        int gameID = gameDAO.addGame(new GameData(0, null, null,
                gameName, new ChessGame()));
        assertNotNull(gameDAO.getGame(gameID));

    }


    @Test
    @Order(16)
    @DisplayName("getGame -")
    public void getGameFail() {
        //needs to be fixed
        int gameID = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        assertNull(gameDAO.getGame(3));
    }

    @Test
    @Order(17)
    @DisplayName("listGame +")
    public void listGameSuccess() {
        int game1 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        int game2 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        int game3 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        assertNotEquals(Map.of(), gameDAO.listGame(), "list is empty");
        assertEquals(3, gameDAO.listGame().size(), "list is empty");
    }


    @Test
    @Order(18)
    @DisplayName("listGame -")
    public void listGameFail() {
        //howto have neg case?
        assertEquals(List.of(), gameDAO.listGame(), "list is empty");
        assertEquals(0, gameDAO.listGame().size(), "list is empty");
    }

    @Test
    @Order(17)
    @DisplayName("updateGame +")
    public void updateGameSuccess() throws InvalidMoveException {
        //Test with moves and playerturns and stuff5
        int game1 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        GameData myGameData = gameDAO.getGame(game1);
        String oldGameString = myGameData.game().getBoard().toString();
        ChessGame.TeamColor oldGameTurn = myGameData.game().getTeamTurn();
        myGameData.game().makeMove(
                new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        gameDAO.updateGame(myGameData);
        assertNotNull(gameDAO.getGame(game1), "Game wasn't added");
        assertNotEquals(oldGameString, gameDAO.getGame(game1).game().getBoard().toString());
        assertNotEquals(oldGameTurn, gameDAO.getGame(game1).game().getTeamTurn());

    }


    @Test
    @Order(18)
    @DisplayName("updateGame -")
    public void updateGameFail() { //too long aname
        String tooLong = "a".repeat(256);
        int game1 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        GameData myGameData = new GameData(game1, tooLong, null, null, null);

        assertThrows(
                RuntimeException.class, () -> gameDAO.updateGame(myGameData), "Didn't throw exception");

    }

    @Test
    @Order(19)
    @DisplayName("clearGame ")
    public void clearGameSuccess() {
        int game1 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        int game2 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        int game3 = gameDAO.addGame(new GameData(47, null, null,
                gameName, new ChessGame()));
        gameDAO.clear();

        assertEquals(List.of(), gameDAO.listGame(), "list is empty");
        assertNotEquals(3, gameDAO.listGame().size(), "list is empty");
    }
}