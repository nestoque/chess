package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final String team;

    public ConnectCommand(String authToken, Integer gameID, String teamColor) {
        super(CommandType.CONNECT, authToken, gameID);
        team = teamColor;
    }

    public String getTeam() {
        return team;
    }
}
