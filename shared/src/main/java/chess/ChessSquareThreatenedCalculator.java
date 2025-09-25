package chess;

import java.util.Set;

public class ChessSquareThreatenedCalculator {

    private static final int[][] DIRECT_DIRECTIONS = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}};
    private static final int[][] DIAGONAL_DIRECTIONS = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
    private static final int[][] SURROUNDING_OFFSETS = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}, {1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
    private static final int[][] KNIGHT_THREATENING_MOVES = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
    private static final Set<ChessPiece.PieceType> DIAGONAL_TAKERS =
            Set.of(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP);
    private static final Set<ChessPiece.PieceType> DIRECT_TAKERS =
            Set.of(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK);

    /**
     * Determines if the given position is threatened
     *
     * @param board     the board in question
     * @param teamColor which team being attacked
     * @param position  position on the board to check
     * @return True if the specified team is in check
     */
    public static boolean isThreatened(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition position) {
        ///do a queen move calc but ignore empties, and then  add a knight calc


        int thisRow = position.getRow();
        int thisCol = position.getColumn();
        int moveToRow = 0, moveToCol = 0;
        ChessPiece myPiece = board.getPiece(position);
        if (checkThreatenDirection(board, teamColor, position, DIAGONAL_DIRECTIONS, DIAGONAL_TAKERS)
                || checkThreatenDirection(board, teamColor, position, DIRECT_DIRECTIONS, DIRECT_TAKERS)
                || checkThreateningKnights(board, teamColor, position)
                || checkThreateningPawn(board, teamColor, position)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public static boolean surroundingSquaresThreatened(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition position) {
        //find king
        int thisRow = position.getRow();
        int thisCol = position.getColumn();
        for (int[] offset : SURROUNDING_OFFSETS) {
            ChessPosition offsetPosition = new ChessPosition(thisRow + offset[0], thisCol + offset[1]);
            if (!isThreatened(board, teamColor, position)) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkThreatenDirection(ChessBoard board, ChessGame.TeamColor teamColor,
                                                 ChessPosition position,
                                                 int[][] threatenDirections,
                                                 Set<ChessPiece.PieceType> threateningPieces) {
        int thisRow = position.getRow();
        int thisCol = position.getColumn();
        int startRow;
        int startCol;
        int currentDistance;
        for (int[] movePair : threatenDirections) {
            startRow = thisRow;
            startCol = thisCol;
            currentDistance = 0;
            while (true) {
                currentDistance += 1;
                startRow += movePair[0];
                startCol += movePair[1];
                if ((8 >= startRow) && (startRow > 0) && (8 >= startCol) && (startCol > 0)) {
                    ChessPosition moveFromPosition = new ChessPosition(startRow, startCol);
                    ChessPiece attackingPiece = board.getPiece((moveFromPosition));

                    //piece exists
                    if (attackingPiece != null) {
                        ChessPiece.PieceType movetoPieceType = attackingPiece.getPieceType();
                        //add if piece is enemy and a good type
                        if (attackingPiece.getTeamColor() != teamColor) {
                            if (threateningPieces.contains(movetoPieceType)) {
                                return true;
                            } else if (currentDistance == 1 && movetoPieceType == ChessPiece.PieceType.KING) {
                                return true;
                            }
                        } else {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }


    public static boolean checkThreateningKnights(ChessBoard board, ChessGame.TeamColor teamColor,
                                                  ChessPosition position) {
        int thisRow = position.getRow();
        int thisCol = position.getColumn();
        int startRow;
        int startCol;
        for (int[] movePair : KNIGHT_THREATENING_MOVES) {
            startRow = thisRow + movePair[0];
            startCol = thisCol + movePair[1];
            if ((8 >= startRow) && (startRow > 0) && (8 >= startCol) && (startCol > 0)) {
                ChessPosition moveToPosition = new ChessPosition(startRow, startCol);
                ChessPiece moveToPiece = board.getPiece((moveToPosition));
                if (moveToPiece != null) {
                    ChessPiece.PieceType movetoPieceType = moveToPiece.getPieceType();
                    if (moveToPiece.getTeamColor() != teamColor && movetoPieceType == ChessPiece.PieceType.KNIGHT) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkThreateningPawn(ChessBoard board, ChessGame.TeamColor teamColor,
                                               ChessPosition position) {

        int thisCol = position.getColumn();
        ChessPiece myPiece = board.getPiece(position);
        int threatRow = switch (teamColor) {
            case WHITE -> {
                yield position.getRow() + 1;
            }
            case BLACK -> {
                yield position.getRow() - 1;
            }
        };

        if (threatRow > 0 && threatRow <= 8) {
            //Attack
            for (int attackCol : new int[]{thisCol + 1, thisCol - 1}) {
                if (attackCol > 0 && attackCol <= 8) {
                    chess.ChessPosition attackDiag = new ChessPosition(threatRow, attackCol);
                    ChessPiece moveToPiece = board.getPiece(attackDiag);
                    if (moveToPiece != null
                            && moveToPiece.getTeamColor() != myPiece.getTeamColor()
                            && moveToPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        return true;

                    }
                }
            }


        }


        return false;
    }


    /**
     * finds the ChessPosition of the King
     *
     * @param teamColor which team's King to find
     * @return True if the specified team is in check
     */
    public static ChessPosition findKing(ChessGame.TeamColor teamColor, ChessBoard board) {
        //find king
        for (int currentCol = 1; currentCol <= 8; currentCol++) {
            for (int currentRow = 1; currentRow <= 8; currentRow++) {
                ChessPosition squareChecked = new ChessPosition(currentRow, currentCol);
                ChessPiece suspectKing = board.getPiece(squareChecked);
                if (suspectKing != null && suspectKing.getPieceType() == ChessPiece.PieceType.KING
                        && suspectKing.getTeamColor() == teamColor) {
                    return squareChecked;
                }
            }
        }
        throw new RuntimeException("No King Fund");
    }


}
