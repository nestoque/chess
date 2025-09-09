package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType;
import static chess.ChessPiece.PieceType.*;

public class PawnMovesCalculator {

    private static ChessPiece.PieceType[] promotionPieces = {KNIGHT, BISHOP, ROOK, QUEEN};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();

        int moveToRow = 0, moveToCol = 0;
        int startRow = 0, promotionRow = 0, moveDirection = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        moveDirection = switch (myPiece.getTeamColor()) {
            case WHITE -> {
                startRow = 1;
                promotionRow = 7;
                yield 1;
            }
            case BLACK -> {
                startRow = 6;
                promotionRow = 0;
                yield 1;
            }
        };

        //Double Jump
        if (thisRow == startRow) {
            chess.ChessPosition doubleJumpPosition = new ChessPosition(thisRow + 2 * moveDirection, thisCol);
            if (board.getPiece(doubleJumpPosition) == null) {
                myMoveList.add(new ChessMove(myPosition, doubleJumpPosition, null));
            }
        }
        //Normal Walk

        //Attack

        return myMoveList;
    }
}
