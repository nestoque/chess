package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {

    private static int[][] RookMoveDirectionsArray = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] movePair : RookMoveDirectionsArray) {
            moveToRow = thisRow;
            moveToCol = thisCol;
            while (true) {
                moveToRow += movePair[0];
                moveToCol += movePair[1];
                if ((8 > moveToRow) && (moveToRow >= 0) && (8 > moveToCol) && (moveToCol >= 0)) {
                    ChessPosition moveToPosition = new ChessPosition(moveToRow, moveToCol);
                    if (board.getPiece(moveToPosition).getTeamColor() != myPiece.getTeamColor()) {
                        myMoveList.add(new ChessMove(myPosition, new ChessPosition(moveToRow, moveToCol), null));
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }

        }
        return myMoveList;
    }

}
