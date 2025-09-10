package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.infinitePieceMoves;

public class RookMovesCalculator {

    private static final int[][] ROOK_MOVE_DIRECTIONS = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return infinitePieceMoves(board, myPosition, ROOK_MOVE_DIRECTIONS);
    }

}
