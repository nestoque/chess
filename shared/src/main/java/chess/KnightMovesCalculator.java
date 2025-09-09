package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {

    private static int[][] KnightMoveOptionsArray = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] movePair : KnightMoveOptionsArray) {
            moveToRow = thisRow + movePair[0];
            moveToCol = thisCol + movePair[1];
            if ((8 >= moveToRow) && (moveToRow > 0) && (8 >= moveToCol) && (moveToCol > 0)) {
                ChessPosition moveToPosition = new ChessPosition(moveToRow, moveToCol);
                if (board.getPiece(moveToPosition) == null || board.getPiece(moveToPosition).getTeamColor() != myPiece.getTeamColor()) {
                    myMoveList.add(new ChessMove(myPosition, new ChessPosition(moveToRow, moveToCol), null));
                }
            }
        }
        return myMoveList;
    }
}
