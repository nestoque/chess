package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.infinitePieceMoves;

public class QueenMovesCalculator {

    private static final int[][] QUEEN_MOVE_DIRECTIONS = {{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};)

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return infinitePieceMoves(board, myPosition, QUEEN_MOVE_DIRECTIONS);
    }

}
