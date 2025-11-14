package ui;

import chess.ChessBoard;

import javax.swing.*;
import java.util.Objects;
import static ui.EscapeSequences.*;


public class DrawBoard {
    private static final int MAX_ROWS = 8+2;
    private static final int MAX_COLS = 8+2;
    private static final String[] LETTER_ROW = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String TEXT_CLR = SET_TEXT_COLOR_WHITE;
    private static final String TEXT_BACKGROUND_CLR = SET_TEXT_COLOR_BLACK;
    private static final String WHITE_SQUARE_CLR =SET_BG_COLOR_LIGHT_GREY;
    private static final String WHITE_PIECE_CLR =SET_TEXT_COLOR_WHITE;
    private static final String BLACK_SQUARE_CLR =SET_TEXT_COLOR_BLACK;
    private static final String BLACK_PIECE_CLR =SET_BG_COLOR_DARK_GREY;

    public static String draw(String teamColor, ChessBoard board) {
        int startRow, endRow, drawDirection;
        if (Objects.equals(teamColor, "BLACK")) {
            startRow = MAX_ROWS;
            endRow = 0;
            drawDirection = -1;
        } else {
            startRow = MAX_ROWS;
            endRow = 0;
            drawDirection = -1;
        }


        String text = board.toString();

        StringBuilder boardString = new StringBuilder();
        for (int row = startRow; row != endRow; row += drawDirection) {
            switch(row) {
                case (0),(MAX_ROWS-1) -> boardString.append(LETTER_ROW);
                default -> boardString.append(drawSquare(, text));
            }
        }


        return boardString.toString();
    }

    private static String getColorFormat(int row, int col) {
        if (row == MAX_ROWS - 1 || col == MAX_COLS -1) {
            return TEXT_BACKGROUND_CLR;
        } else {
            return (row % 2 == 0 && col % 2 == 0) ? WHITE_PIECE_CLR + WHITE_SQUARE_CLR : BLACK_PIECE_CLR + BLACK_SQUARE_CLR;
        }
    }

    private static String drawSquare(String txt, String colorFormatter) {
        return colorFormatter + txt;
    }

    private enum DrawPerspective {
        WHITE,
        BLACK
    }
}
