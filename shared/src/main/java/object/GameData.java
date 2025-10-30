package object;

import chess.ChessGame;

public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game) {

    public GameData setWhiteUsername(String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }

    public GameData setBlackUsername(String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }

    public GameData setGameID(int newGameID) {
        return new GameData(newGameID, whiteUsername, blackUsername, gameName, game);
    }
}
