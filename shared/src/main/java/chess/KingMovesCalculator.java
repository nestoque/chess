package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.finitePieceMoves;


public class KingMovesCalculator {

    private static final int[][] KING_MOVES = {{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};


    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        return finitePieceMoves(board, myPosition, KING_MOVES);
    }
}
