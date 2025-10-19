package dataaccess;

public interface AuthDAO {
    String addAuth();

    AuthData getAuth();

    void deleteAuth();

    void clear();
}
