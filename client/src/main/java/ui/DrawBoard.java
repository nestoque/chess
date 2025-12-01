package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;


public class DrawBoard {
    private static final int MAX_ROWS = 8 + 2;
    private static final int MAX_COLS = 8 + 2;
    private static final String[] LETTER_ROW = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String TEXT_CLR = SET_TEXT_COLOR_WHITE;
    private static final String TEXT_BACKGROUND_CLR = SET_BG_COLOR_DARK_GREY;
    private static final String WHITE_SQUARE_CLR = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE_CLR = SET_TEXT_COLOR_BLUE;
    private static final String BLACK_SQUARE_CLR = SET_BG_COLOR_WHITE;
    private static final String BLACK_PIECE_CLR = SET_TEXT_COLOR_MAGENTA;
    private static final String HIGHLIGHT_SQUARE_CLR = SET_BG_COLOR_GREEN;
    private static final String RESET_ALL = RESET_TEXT_COLOR + RESET_BG_COLOR;
    private static final int LETTERLEN = LETTER_ROW.length;

    public static String draw(String teamColor, ChessBoard board, Collection<ChessMove> moves) {
        int startRow, endRow, drawDirection;
        SquareColor thisSquareColor = SquareColor.WHITE;
        int colLetterStart, colLetterEnd;
        if (Objects.equals(teamColor, "BLACK")) {
            startRow = MAX_ROWS - 1;
            endRow = -1;
            drawDirection = -1;
            colLetterStart = LETTERLEN - 1;
            colLetterEnd = -1;

        } else {
            startRow = 0;
            endRow = MAX_ROWS;
            drawDirection = 1;
            colLetterStart = 0;
            colLetterEnd = LETTERLEN;
        }
        boolean[] coveredStartPosition = {false};

        String[] text = board.toString().replace("\n", "").substring(1).split("[|]+");
        StringBuilder boardString = new StringBuilder();
        for (int row = startRow; row != endRow; row += drawDirection) {
            switch (row) {
                case (0), (MAX_ROWS - 1) -> boardString.append(topBottom(colLetterStart, colLetterEnd, drawDirection));
                default -> boardString.append(middleRows(row, text, thisSquareColor,
                        startRow, endRow, drawDirection, moves, coveredStartPosition, teamColor));
            }
            boardString.append(RESET_ALL + "\n");
            if (thisSquareColor == SquareColor.WHITE) {
                thisSquareColor = SquareColor.BLACK;
            } else {
                thisSquareColor = SquareColor.WHITE;
            }
        }


        return boardString.toString();
    }

    private static String getColorFormat(String piece, SquareColor thisColor) {
        //something about
        //for (ChessMove m : moves ) {
        //                        if (m.getEndPosition().equals(new ChessPosition(row,col)))
        //                    }
        String foregroundColor = (Character.isUpperCase(piece.charAt(0))) ? WHITE_PIECE_CLR : BLACK_PIECE_CLR;
        String backgroundColor = switch (thisColor) {
            case WHITE -> WHITE_SQUARE_CLR;
            case BLACK -> BLACK_SQUARE_CLR;
            case HIGHLIGHT -> HIGHLIGHT_SQUARE_CLR;
        };
        return foregroundColor + backgroundColor;
    }

    private static String drawSquare(String txt, String colorFormatter) {
        return colorFormatter + txt;
    }

    private enum SquareColor {
        WHITE,
        BLACK,
        HIGHLIGHT
    }

    private static String pickPiece(String text) {
        return switch (text) {
            case "K" -> WHITE_KING;
            case "Q" -> WHITE_QUEEN;
            case "B" -> WHITE_BISHOP;
            case "N" -> WHITE_KNIGHT;
            case "R" -> WHITE_ROOK;
            case "P" -> WHITE_PAWN;
            case "k" -> BLACK_KING;
            case "q" -> BLACK_QUEEN;
            case "b" -> BLACK_BISHOP;
            case "n" -> BLACK_KNIGHT;
            case "r" -> BLACK_ROOK;
            case "p" -> BLACK_PAWN;
            default -> EMPTY;
        };
    }

    private static String topBottom(int colLetterStart, int colLetterEnd, int drawDirection) {
        StringBuilder boardString = new StringBuilder();
        boardString.append(TEXT_CLR + TEXT_BACKGROUND_CLR + EMPTY);
        for (int col = colLetterStart; col != colLetterEnd; col += drawDirection) {
            boardString.append(" " + LETTER_ROW[col] + "\u2003");
        }
        boardString.append(EMPTY);
        return boardString.toString();
    }

    private static String middleRows(int row, String[] text, SquareColor thisSquareColor,
                                     int startCol, int endCol, int drawDirection, Collection<ChessMove> moves,
                                     boolean[] coveredStartPosition, String teamColor) {
        StringBuilder boardString = new StringBuilder();
        for (int col = startCol; col != endCol; col += drawDirection) {
            if (col == 0 || col == MAX_COLS - 1) {
                boardString.append(drawSquare(" " + Integer.toString(9 - row) + "\u2003",
                        TEXT_CLR + TEXT_BACKGROUND_CLR));
            } else {
                String thisCharacter = text[(row - 1) * (MAX_COLS - 2) + (col - 1)];
//                boardString.append(drawSquare((thisCharacter == " ") ? EMPTY : " " + thisCharacter + " ",
//                        getColorFormat(thisCharacter, thisSquareColor)));
                if (moves != null && isHighlightSquare(row, col, moves, coveredStartPosition, teamColor)) {
                    boardString.append(drawSquare(pickPiece(thisCharacter), getColorFormat(thisCharacter, SquareColor.HIGHLIGHT)));
                } else {
                    boardString.append(drawSquare(pickPiece(thisCharacter), getColorFormat(thisCharacter, thisSquareColor)));
                }
                if (thisSquareColor == SquareColor.WHITE) {
                    thisSquareColor = SquareColor.BLACK;
                } else {
                    thisSquareColor = SquareColor.WHITE;
                }
            }

        }
        return boardString.toString();
    }

    private static boolean isHighlightSquare(int row, int col, Collection<ChessMove> moves, boolean[] coveredStartPosition,
                                             String teamColor) {

        //Translate To ChessPosition Number System
        int absRow = 9 - row;
        int absCol = col;
        if (!coveredStartPosition[0]) {
            ChessMove firstMove = moves.iterator().next();
            ChessPosition firstStartPos = firstMove.getStartPosition();
            if (firstStartPos.getRow() == absRow && firstStartPos.getColumn() == absCol) {
                coveredStartPosition[0] = true;
                return true;
            }
        }
        for (ChessMove m : moves) {
            ChessPosition endPos = m.getEndPosition();
            if (endPos.getRow() == absRow && endPos.getColumn() == absCol) {
                return true;
            }
        }

        return false;
    }

}
