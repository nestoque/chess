package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessSquareThreatenedCalculator.findKing;
import static chess.ChessSquareThreatenedCalculator.isThreatened;
import static java.util.List.copyOf;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard gameBoard;
    TeamColor teamTurn;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
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
        ChessGame.TeamColor teamColor = myPiece.getTeamColor();
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
                if (placePieceChecked != null && myPiece.getTeamColor() == teamColor) {
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
            switch (thisTeam) {
                case WHITE -> setTeamTurn(TeamColor.BLACK);
                case BLACK -> setTeamTurn(TeamColor.WHITE);
            }
            gameBoard = makeLegalMove(move);
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
        return ChessSquareThreatenedCalculator.isThreatened(gameBoard, teamColor, findKing(teamColor, gameBoard));
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
        return ChessSquareThreatenedCalculator.isThreatened(gameBoard, teamColor, kingPosition)
                && ChessSquareThreatenedCalculator.surroundingSquaresThreatened(gameBoard, teamColor, kingPosition);
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
        ChessPiece myPiece = newBoard.getPiece(myStartPosition);
        newBoard.removePiece(myStartPosition);
        ChessPiece.PieceType promotePieceType = move.getPromotionPiece();
        if (promotePieceType != null) {
            newBoard.addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(), promotePieceType));
        } else {
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

