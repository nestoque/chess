package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //organize sessions by game idea
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        Connection connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(Integer targetGameID, String excludeAuthToken, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(targetGameID)) {
                    if (!c.authToken.equals(excludeAuthToken)) {
                        c.session.getRemote().sendString(msg);
                    }
                }
            }
        }
    }

    public void add(int gameID, Connection connection) {
    }
}
