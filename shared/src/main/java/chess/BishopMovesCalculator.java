package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.infinitePieceMoves;

public class BishopMovesCalculator {


    private static final int[][] BISHOP_MOVE_DIRECTIONS = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return infinitePieceMoves(board, myPosition, BISHOP_MOVE_DIRECTIONS);
    }
}
