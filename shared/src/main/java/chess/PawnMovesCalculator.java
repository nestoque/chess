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
                startRow = 2;
                promotionRow = 8;
                yield 1;
            }
            case BLACK -> {
                startRow = 7;
                promotionRow = 1;
                yield -1;
            }
        };


        //Normal Walk
        int movedRow = thisRow + moveDirection;
        chess.ChessPosition movedRowPosition = new ChessPosition(movedRow, thisCol);
        if (board.getPiece(movedRowPosition) == null) {
            if (movedRow == promotionRow) {
                for (PieceType promotionPiece : promotionPieces) {
                    myMoveList.add(new ChessMove(myPosition, movedRowPosition, promotionPiece));
                }
            } else {
                myMoveList.add(new ChessMove(myPosition, movedRowPosition, null));

                //Double Jump
                if (thisRow == startRow) {
                    chess.ChessPosition doubleJumpPosition = new ChessPosition(thisRow + 2 * moveDirection, thisCol);
                    if (board.getPiece(doubleJumpPosition) == null) {
                        myMoveList.add(new ChessMove(myPosition, doubleJumpPosition, null));
                    }
                }
            }

        }
        //Attack
        if (thisCol - 1 > 0) {
            chess.ChessPosition attackLeft = new ChessPosition(movedRow, thisCol - 1);
            if (board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() != myPiece.getTeamColor()) {
                if (movedRow == promotionRow) {
                    for (PieceType promotionPiece : promotionPieces) {
                        myMoveList.add(new ChessMove(myPosition, attackLeft, promotionPiece));
                    }
                } else {
                    myMoveList.add(new ChessMove(myPosition, attackLeft, null));
                }

            }
        }

        if (thisCol + 1 <= 8) {
            chess.ChessPosition attackRight = new ChessPosition(movedRow, thisCol + 1);
            if (board.getPiece(attackRight) != null && board.getPiece(attackRight).getTeamColor() != myPiece.getTeamColor()) {
                if (movedRow == promotionRow) {
                    for (PieceType promotionPiece : promotionPieces) {
                        myMoveList.add(new ChessMove(myPosition, attackRight, promotionPiece));
                    }
                } else {
                    myMoveList.add(new ChessMove(myPosition, attackRight, null));
                }

            }
        }


        return myMoveList;
    }
}
