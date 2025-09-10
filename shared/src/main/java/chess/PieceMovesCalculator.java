package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        return switch (myPiece.getPieceType()) {
            case KING -> KingMovesCalculator.pieceMoves(board, myPosition);
            case QUEEN -> QueenMovesCalculator.pieceMoves(board, myPosition);
            case BISHOP -> BishopMovesCalculator.pieceMoves(board, myPosition);
            case KNIGHT -> KnightMovesCalculator.pieceMoves(board, myPosition);
            case ROOK -> RookMovesCalculator.pieceMoves(board, myPosition);
            case PAWN -> PawnMovesCalculator.pieceMoves(board, myPosition);
        };
    }

    public static Collection<ChessMove> infinitePieceMoves(ChessBoard board, ChessPosition myPosition, int[][] moveDirections) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] movePair : moveDirections) {
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

    public static Collection<ChessMove> finitePieceMoves(ChessBoard board, ChessPosition myPosition, int[][] moveOptionArray) {
        Collection<ChessMove> myMoveList = new ArrayList<>();
        int thisRow = myPosition.getRow();
        int thisCol = myPosition.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] movePair : moveOptionArray) {
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
