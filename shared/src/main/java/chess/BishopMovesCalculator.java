package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {


    private static int[][] BishopMoveDirectionsArray = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] movePair : BishopMoveDirectionsArray) {
            moveToRow = thisRow;
            moveToCol = thisCol;
            while (true) {
                moveToRow += movePair[0];
                moveToCol += movePair[1];
                if ((8 >= moveToRow) && (moveToRow > 0) && (8 >= moveToCol) && (moveToCol > 0)) {
                    ChessPosition moveToPosition = new ChessPosition(moveToRow, moveToCol);
                    if (board.getPiece(moveToPosition) == null) {
                        myMoveList.add(new ChessMove(myPosition, new ChessPosition(moveToRow, moveToCol), null));
                    } else if (board.getPiece(moveToPosition).getTeamColor() != myPiece.getTeamColor()) {
                        myMoveList.add(new ChessMove(myPosition, new ChessPosition(moveToRow, moveToCol), null));
                        break;
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
