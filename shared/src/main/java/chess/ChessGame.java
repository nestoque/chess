package chess;

import java.util.Collection;

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
        if (myPiece == null) {
            return null;
        } else {
            return myPiece.pieceMoves(gameBoard, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     * if legal
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor thisTeam = getTeamTurn();
        ChessBoard tempBoard;
        if (validMoves(move.getStartPosition()).contains(move)) {
            if (isInCheck(thisTeam)) {
                tempBoard = makeLegalMove(move);

            } else {
                //otherwise just execute if in validmoves

                switch (thisTeam) {
                    case WHITE -> setTeamTurn(TeamColor.BLACK);
                    case BLACK -> setTeamTurn(TeamColor.WHITE);
                }
                gameBoard = makeLegalMove(move);

            }
            return;
        }
        throw new InvalidMoveException("Invalid Move");
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find king
        //is threatened
        //return ChessSquareThreatenedCalculator.isThreatened(gameBoard, teamColor, kingPosition)
        throw new RuntimeException("Not implemented");
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        //threatened each square around and this square
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        /* threaten each square around and not isincheck//not this square
         *
         * OR
         *
         * no valid moves
         *
         * */
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
        ChessBoard newBoard = gameBoard;
        ChessPosition myStartPosition = move.getStartPosition();
        ChessPiece myPiece = newBoard.getPiece(myStartPosition);
        newBoard.removePiece(myStartPosition);
        newBoard.addPiece(move.getEndPosition(), myPiece);
        return newBoard;
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

