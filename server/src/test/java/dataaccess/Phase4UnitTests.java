package dataaccess;

import chess.ChessGame;
import object.AuthData;
import object.GameData;
import object.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import responses.*;
import service.*;
import utils.TokenUtils;

import java.sql.SQLException;

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
    public void AddAuthSuccess() {
        assertTrue(authDAO.addAuth(new AuthData(testToken, username)));
    }

    @Test
    @Order(2)
    @DisplayName("AddAuth -")
    public void AddAuthFail() {
        AuthData testUser = new AuthData(testToken, username);
        authDAO.addAuth(testUser);
        assertFalse(authDAO.addAuth(new AuthData(testToken, username)), "addAuth didn't return false on repeated user");
    }


    @Test
    @Order(3)
    @DisplayName("getAuth +")
    public void getAuthSuccess() throws ServiceException {
        authDAO.addAuth(new AuthData(testToken, username));
        assertNotNull(authDAO.getAuth(testToken));
    }


    @Test
    @Order(4)
    @DisplayName("getAuth -")
    public void getAuthFail() throws ServiceException {
        authDAO.addAuth(new AuthData(testToken, username));
        assertNull(authDAO.getAuth("othertoken"));
    }


    @Test
    @Order(5)
    @DisplayName("deleteAuth +")
    public void deleteAuthSuccess() throws ServiceException {
        authDAO.addAuth(new AuthData(testToken, username));
        authDAO.deleteAuth(testToken);
        assertNull(authDAO.getAuth(testToken));
    }


    @Test
    @Order(6)
    @DisplayName("deleteAuth -")
    public void deleteAuthFail() throws ServiceException {
        authDAO.addAuth(new AuthData(testToken, username));

        SQLException exception = exception = assertThrows(
                SQLException.class, () -> authDAO.deleteAuth("othertoken"), "Didn't throw exception");
    }


    @Order(7)
    @DisplayName("authClear")
    public void authClearSuccess() throws ServiceException {

    }


    @Test
    @Order(8)
    @DisplayName("addUser +")
    public void addUserSuccess() throws ServiceException {

    }


    @Test
    @Order(9)
    @DisplayName("addUser -")
    public void addUserFail() throws ServiceException {

    }


    @Test
    @Order(10)
    @DisplayName("getUser +")
    public void getUserSuccess() throws ServiceException {

    }


    @Test
    @Order(11)
    @DisplayName("getUser -")
    public void getUserFail() throws ServiceException {


    }


    @Test
    @Order(12)
    @DisplayName("clearUser")
    public void clearUserSuccess() throws ServiceException {

    }


    @Test
    @Order(13)
    @DisplayName("addGame +")
    public void addGameSuccess() throws ServiceException {


    }


    @Test
    @Order(14)
    @DisplayName("addGame -")
    public void addGameFail() throws ServiceException {

    }

    @Test
    @Order(15)
    @DisplayName("getGame +")
    public void getGameSuccess() throws ServiceException {


    }


    @Test
    @Order(16)
    @DisplayName("getGame -")
    public void getGameFail() throws ServiceException {

    }

    @Test
    @Order(17)
    @DisplayName("listGame +")
    public void listGameSuccess() throws ServiceException {


    }


    @Test
    @Order(18)
    @DisplayName("listGame -")
    public void listGameFail() throws ServiceException {

    }

    @Test
    @Order(17)
    @DisplayName("updateGame +")
    public void updateGameSuccess() throws ServiceException {


    }


    @Test
    @Order(18)
    @DisplayName("updateGame -")
    public void updateGameFail() throws ServiceException {

    }

    @Test
    @Order(19)
    @DisplayName("clearGame ")
    public void clearGameSuccess() throws ServiceException {

    }
}