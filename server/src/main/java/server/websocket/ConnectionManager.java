package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //organize sessions by game idea
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Integer targetGameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Map.Entry<Session, Integer> entry : connections.entrySet()) {
            Session sesh = entry.getKey();
            Integer id = entry.getValue();
            if (sesh.isOpen() && id.equals(targetGameID)) {
                if (!sesh.equals(excludeSession)) {
                    sesh.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastAllInGame(Integer targetGameID, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Map.Entry<Session, Integer> entry : connections.entrySet()) {
            Session sesh = entry.getKey();
            Integer id = entry.getValue();
            if (sesh.isOpen() && id.equals(targetGameID)) {
                sesh.getRemote().sendString(msg);
            }
        }
    }

}
