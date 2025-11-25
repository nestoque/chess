package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String mes) {
        super(ServerMessageType.NOTIFICATION);
        message = mes;
    }

    public String getMessage() {
        return message;
    }
}
