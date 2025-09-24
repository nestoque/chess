package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessSquareThreatenedCalculator {

    private static final int[][] GENERAL_THREATEN_DIRECTIONS = {{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};
    private static final int[][] KNIGHT_THREATENING_MOVES = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};

    /**
     * Determines if the given position is threatened
     *
     * @param board     the board in question
     * @param teamColor which team being attacked
     * @param position  position on the board to check
     * @return True if the specified team is in check
     */
    public static boolean isThreatened(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition position) {
        ///do a queen move calc but ignore empties, and then  add a knight calc

        ;
        int thisRow = position.getRow();
        int thisCol = position.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(position);
        for (int[] movePair : GENERAL_THREATEN_DIRECTIONS) {
            moveToRow = thisRow;
            moveToCol = thisCol;


            while (true) {
                moveToRow += movePair[0];
                moveToCol += movePair[1];
                if ((8 >= moveToRow) && (moveToRow > 0) && (8 >= moveToCol) && (moveToCol > 0)) {
                    ChessPosition moveToPosition = new ChessPosition(moveToRow, moveToCol);
                    if (board.getPiece(moveToPosition) == null) {
                        myMoveList.add(new ChessMove(position, new ChessPosition(moveToRow, moveToCol), null));
                    } else if (board.getPiece(moveToPosition).getTeamColor() != myPiece.getTeamColor()) {
                        myMoveList.add(new ChessMove(position, new ChessPosition(moveToRow, moveToCol), null));
                        break;
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }

        }
        //Pawn


        //Knight

        if (true) {
            return true;
        }
        return false;
    }
}
