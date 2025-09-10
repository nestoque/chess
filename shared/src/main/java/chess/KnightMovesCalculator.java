package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.finitePieceMoves;

public class KnightMovesCalculator {

    private static final int[][] KNIGHT_MOVES = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return finitePieceMoves(board, myPosition, KNIGHT_MOVES);
    }
}
