package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove newMove) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        move = newMove;
    }

    public ChessMove getMove() {
        return move;
    }
}
