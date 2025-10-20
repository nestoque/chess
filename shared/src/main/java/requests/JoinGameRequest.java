package requests;

import chess.ChessGame;

public record JoinGameRequest(
        ChessGame.TeamColor playerColor,
        int gameID) {
    //{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
}
