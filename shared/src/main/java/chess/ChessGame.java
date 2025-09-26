package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessSquareThreatenedCalculator.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private static final ChessPosition WHITE_KING_POSITION = new ChessPosition(1, 5);
    private static final ChessPosition WHITE_KING_QUEENSIDE_POSITION = new ChessPosition(1, 7);
    private static final ChessPosition WHITE_KING_KINGSIDE_CASTLE_POSITION = new ChessPosition(1, 3);
    private static final ChessMove WHITE_QUEENSIDE_CASTLE = new ChessMove(WHITE_KING_POSITION, WHITE_KING_QUEENSIDE_POSITION, null);
    private static final ChessMove WHITE_KINGSIDE_CASTLE = new ChessMove(WHITE_KING_POSITION, WHITE_KING_KINGSIDE_CASTLE_POSITION, null);
    private static final ChessPosition BLACK_KING_POSITION = new ChessPosition(8, 5);
    private static final ChessPosition BLACK_KING_QUEENSIDE_CASTLE_POSITION = new ChessPosition(8, 3);
    private static final ChessPosition BLACK_KING_KINGSIDE_CASTLE_POSITION = new ChessPosition(8, 7);
    private static final ChessMove BLACK_QUEENSIDE_CASTLE = new ChessMove(BLACK_KING_POSITION, BLACK_KING_QUEENSIDE_CASTLE_POSITION, null);
    private static final ChessMove BLACK_KINGSIDE_CASTLE = new ChessMove(BLACK_KING_POSITION, BLACK_KING_KINGSIDE_CASTLE_POSITION, null);
    //private static final Set<ChessPiece.PieceType> CASTLE_PIECES = Set.of(ChessPiece.PieceType.KING, ChessPiece.PieceType.ROOK);

    ChessBoard gameBoard;
    TeamColor teamTurn;
    boolean[] queensideCastlePiecesHaventMoved; // use teamColor enum for indices
    boolean[] kingsideCastlePiecesHaventMoved;
    ChessMove lastMove; //for en_passant calculations

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        queensideCastlePiecesHaventMoved = new boolean[]{true, true};
        kingsideCastlePiecesHaventMoved = new boolean[]{true, true};
        lastMove = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();
        if (myPiece == null) {
            return validMoveList;
        }
        TeamColor teamColor = myPiece.getTeamColor();
        Collection<ChessMove> pieceCapableMovesList = myPiece.pieceMoves(gameBoard, startPosition);
        //ChessBoard oldBoard = gameBoard;
        ChessBoard theoreticalBoard;
        //only return the moves if they are not in check afterward
        for (ChessMove thisMove : pieceCapableMovesList) {
            theoreticalBoard = makeLegalMove(thisMove);
            if (!isThreatened(theoreticalBoard, teamColor, findKing(teamColor, theoreticalBoard))) {
                validMoveList.add(thisMove);
            }
        }
        //castling
        switch (myPiece.getPieceType()) {
            case KING -> {
                if (startPosition == WHITE_KING_POSITION && teamColor == TeamColor.WHITE) {
                    //Check if any square in between is in check,
                    //Check that every square in between is empty

                    if (kingsideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()]) {
                        validMoveList.add(WHITE_KINGSIDE_CASTLE);
                    }
                    if (queensideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()]) {
                        validMoveList.add(WHITE_QUEENSIDE_CASTLE);
                    }
                    ;
                    ;

                } else if (startPosition == BLACK_KING_POSITION && teamColor == TeamColor.BLACK) {
                    if (kingsideCastlePiecesHaventMoved[TeamColor.BLACK.ordinal()]) {
                        validMoveList.add(BLACK_KINGSIDE_CASTLE);
                    }
                    if (queensideCastlePiecesHaventMoved[TeamColor.BLACK.ordinal()]) {
                        validMoveList.add(BLACK_QUEENSIDE_CASTLE);
                    }
                    ;
                }
            }
            case PAWN -> { //en_passant
                /* if this is a pawn,
                 this row is same as last move row,
                  and that row is the jump row of a pawn,
                  and
                  check if its a pawn
                *  */
                if (lastMove != null && lastMove.getEndPosition().getRow() == startPosition.getRow()) {
                    int moveDirection;
                    int possibleJumpRow = switch (teamColor) {
                        case BLACK -> {
                            moveDirection = -1;
                            yield 4;
                        }
                        case WHITE -> {
                            moveDirection = 1;
                            yield 5;
                        }
                    };
                    if (gameBoard.getPiece(lastMove.getEndPosition()).getPieceType() == PAWN
                            && startPosition.getRow() == possibleJumpRow
                            && (lastMove.getEndPosition().getColumn() < startPosition.getColumn() + 1
                            || lastMove.getEndPosition().getColumn() > startPosition.getColumn() - 1)) {
                        validMoveList.add(new ChessMove(startPosition,
                                new ChessPosition(startPosition.getRow() + moveDirection, lastMove.getEndPosition().getColumn()), null));
                    }
                }
            }
            case null, default -> {
                //pass
            }
        }
        return validMoveList;
    }

    /**
     * calculates valid moves from all pieces on a team
     *
     * @param teamColor color of team to check
     * @return true if none, false if any
     */
    public boolean checkNoValidMoves(TeamColor teamColor) {
        for (int currentCol = 1; currentCol <= 8; currentCol++) {
            for (int currentRow = 1; currentRow <= 8; currentRow++) {
                ChessPosition placePieceChecked = new ChessPosition(currentRow, currentCol);
                ChessPiece myPiece = gameBoard.getPiece(placePieceChecked);
                if (myPiece != null && myPiece.getTeamColor() == teamColor) {
                    if (!validMoves(placePieceChecked).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Makes a move in a chess game
     * and changes turn
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor thisTeam = getTeamTurn();
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece pieceToMove = gameBoard.getPiece(startPosition);
        if (validMoves(startPosition).contains(move) && pieceToMove.getTeamColor() == thisTeam) {
            //check if castleing pieces have moved
            ChessPiece.PieceType thisPieceType = pieceToMove.getPieceType();
            switch (thisPieceType) {
                case KING -> {
                    if (move.getStartPosition() == WHITE_KING_POSITION && thisTeam == TeamColor.WHITE) {
                        //if type is king, remove both for team
                        kingsideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()] = false;
                        queensideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()] = false;

                    } else if (move.getStartPosition() == BLACK_KING_POSITION && thisTeam == TeamColor.BLACK) {
                        kingsideCastlePiecesHaventMoved[TeamColor.BLACK.ordinal()] = false;
                        queensideCastlePiecesHaventMoved[TeamColor.BLACK.ordinal()] = false;
                    }
                }
                case ROOK -> {
                    if (startPosition.getRow() == 1 && thisTeam == TeamColor.WHITE) {
                        switch (startPosition.getColumn()) {
                            case 1 -> {
                                queensideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()] = false;
                            }
                            case 8 -> {
                                kingsideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()] = false;
                            }
                        }
                    } else if (startPosition.getRow() == 8 && thisTeam == TeamColor.BLACK) {
                        switch (startPosition.getColumn()) {
                            case 1 -> {
                                queensideCastlePiecesHaventMoved[TeamColor.BLACK.ordinal()] = false;
                            }
                            case 8 -> {
                                kingsideCastlePiecesHaventMoved[TeamColor.WHITE.ordinal()] = false;
                            }
                        }
                    }
                }
            }

            switch (thisTeam) {
                case WHITE -> setTeamTurn(TeamColor.BLACK);
                case BLACK -> setTeamTurn(TeamColor.WHITE);
            }
            gameBoard = makeLegalMove(move);
            lastMove = move;

        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isThreatened(gameBoard, teamColor, findKing(teamColor, gameBoard));
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //find king
        ChessPosition kingPosition = findKing(teamColor, gameBoard);
        //threatened each square around and this square
        return isThreatened(gameBoard, teamColor, kingPosition)
                && surroundingSquaresThreatened(gameBoard, teamColor, kingPosition)
                && checkNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //no valid moves
        if (isInCheck(teamColor)) {
            return false;
        }
        return checkNoValidMoves(teamColor);


        //find king
//        ChessPosition kingPosition = findKing(teamColor);
//        //threatened each square around and this square
//        return ChessSquareThreatenedCalculator.isThreatened(gameBoard, teamColor, kingPosition)
//                && ChessSquareThreatenedCalculator.surroundingSquaresThreatened(gameBoard, teamColor, kingPosition);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    /**
     * makes the move and changes turns
     * in a chess game
     *
     * @param move the move to execute
     */
    public ChessBoard makeLegalMove(ChessMove move) {
        ChessBoard newBoard = new ChessBoard(gameBoard.squares);
        ChessPosition myStartPosition = move.getStartPosition();
        ChessPosition myEndPosition = move.getEndPosition();
        ChessPiece myPiece = newBoard.getPiece(myStartPosition);
        newBoard.removePiece(myStartPosition);
        ChessPiece.PieceType promotePieceType = move.getPromotionPiece();
        if (promotePieceType != null) {
            newBoard.addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(), promotePieceType));
        } else {
            switch (myPiece.getPieceType()) {
                case KING -> {/*
                     add castle
                    if KING delta_col is more than 1,
                       remove rook, addpiece rook
                    */
                    int startCol = myStartPosition.getColumn();
                    int endCol = myEndPosition.getColumn();
                    if (Math.abs(startCol - endCol) < 0) {
                        switch (teamTurn) {
                            case BLACK -> {
                                if (move == BLACK_QUEENSIDE_CASTLE) {
                                    newBoard.removePiece(new ChessPosition(8, 1));
                                } else if (move == BLACK_KINGSIDE_CASTLE) {
                                    newBoard.removePiece(new ChessPosition(8, 8));
                                }

                            }
                            case WHITE -> {
                                if (move == WHITE_QUEENSIDE_CASTLE) {
                                    newBoard.removePiece(new ChessPosition(1, 1));
                                } else if (move == WHITE_KINGSIDE_CASTLE) {
                                    newBoard.removePiece(new ChessPosition(1, 8));
                                }
                            }
                        }
                    }

                }

                case PAWN -> {
                    // en passant
                    // if not same col, and null piece there, then remove the piece - direction
                    int jumpedRow = switch (teamTurn) {
                        case BLACK -> +1;
                        case WHITE -> -1;
                    };
                    newBoard.removePiece(new ChessPosition(myStartPosition.getRow(), myEndPosition.getColumn()));
                }
                default -> {
                }
            }
            newBoard.addPiece(move.getEndPosition(), myPiece);
        }
        return newBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, teamTurn);
    }
}


/* team var can castle, bool llowers if rook has moved, maybe even per sid
 * team var, king location?
 *
 * Ischeck, calls isthreatened
 * isthreatened??? loop  directions until meet piece, if other team and can move that direction say yet
 * stalemate, king not in check, but every surrounding place either ischeck or piece occupied
 * maybe make a valid moves function? but then need like linked list to all pieces
 *
 * for en passant, maybe a last game state is saved? use get board, setboard
 *
 * for move, update the gameboard
 *
 *
 * en passant, just adding to calculator, if last game state shows them jumping, and you can attack an empty diagonal
 *  */

