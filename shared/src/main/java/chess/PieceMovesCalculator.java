package chess;

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
}
