package mindchess.model.gameStates;

import mindchess.model.enums.ChessColor;
import mindchess.model.*;
import mindchess.model.pieces.IPiece;
import mindchess.observers.GameStateObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static mindchess.model.enums.ChessColor.BLACK;
import static mindchess.model.enums.ChessColor.WHITE;
import static mindchess.model.enums.PieceType.KING;
import static mindchess.model.enums.SquareType.*;

/**
 * The state which represent when a piece has been selected and the next input will try to move the selected piece to the inputted square
 * <p>
 * From this state you can go to the no piece selected state if you make a move or deselects the piece by inputting an invalid input
 * And Game over state if you fulfill a win condition
 * And Pawn promotion if you move a pawn to the opposite side
 * <p>
 * Each state has to have a game context, a list of legal moves the current player can make, a list of plies to know what previous move has been made and a board.
 * The piece selected game state also needs a square which represent what piece is selected
 *
 * @author Erik Wessman, Elias Carlson, Elias Hallberg, Arvid Holmqvist
 */
public class GameStatePieceSelected implements GameState {

    private final Square selectedSquare;
    private final IGameContext context;
    private IPiece takenPiece = null;
    private final List<GameStateObserver> gameStateObservers = new ArrayList<>();
    private final List<Square> legalSquares;
    private final List<Ply> plies;
    private final IBoard board;

    GameStatePieceSelected(Square selectedSquare, IBoard board, List<Ply> plies, List<Square> legalSquares, IGameContext context) {
        this.selectedSquare = selectedSquare;
        this.board = board;
        this.legalSquares = legalSquares;
        this.plies = plies;
        this.context = context;
    }

    /**
     * Checks if the latest click was on a square that is legal to move to
     * If it is, the move is made
     * If a win condition is met you will win the game
     * If you reach the opposite board the pawn promotion will happen
     *
     * @param x the horizontal chess coordinate of the input
     * @param y the vertical chess coordinate of the input
     */
    @Override
    public void handleInput(int x, int y) {
        Square targetSquare = new Square(x, y);

        if (switchSelectedPieceIfSameColor(targetSquare)) return;

        if (clickedOnSameSquare(targetSquare)){
            context.setGameState(GameStateFactory.createGameStateNoPieceSelected(board, plies, legalSquares, context));
            gameStateObservers.forEach(context::addGameStateObserver);
        }

        if (legalSquares.contains(targetSquare)) {
            targetSquareIsLegal(targetSquare);
        }

        clearAndDrawLegalMoves();
    }

    private boolean clickedOnSameSquare(Square targetSquare) {
        return targetSquare.equals(selectedSquare);
    }

    /**
     * If a player tries to select on one of their pieces besides the selected one, that piece becomes the new selected piece
     * @param targetSquare the square the player is attempting to select
     * @return true if a switch was made, false if not
     */
    private boolean switchSelectedPieceIfSameColor(Square targetSquare) {
        if (board.isAPieceOnSquare(targetSquare) && !targetSquare.equals(selectedSquare) && board.pieceOnSquareColorEquals(targetSquare, context.getCurrentPlayerColor())) {
            clearAndDrawLegalMoves();
            context.setGameState(GameStateFactory.createGameStateNoPieceSelected(board, plies, legalSquares, context));
            gameStateObservers.forEach(context::addGameStateObserver);
            context.handleBoardInput(targetSquare.getX(), targetSquare.getY());
            return true;
        }
        return false;
    }

    /**
     * Moves the selected piece to the target square.
     *   - If any special rules apply to a specific move, they are performed
     *
     * @param targetSquare the square to move the selected piece to
     */
    private void targetSquareIsLegal(Square targetSquare) {
        targetSquare = getLegalSquareByCoordinates(targetSquare.getX(), targetSquare.getY());
        move(selectedSquare,targetSquare);
        addMoveToPlies(selectedSquare, targetSquare);
        notifyDrawPieces();

        if (checkKingTaken()) {
            context.setGameState(GameStateFactory.createGameStateGameOver(context.getCurrentPlayerName() + " has won the game"));
            return;
        }

        if (checkPawnPromotion(targetSquare)) {
            context.setGameState(GameStateFactory.createGameStatePawnPromotion(targetSquare, board, plies, legalSquares, context));
            gameStateObservers.forEach(context::addGameStateObserver);
            clearAndDrawLegalMoves();
            return;
        }

        notifySwitchPlayer();
        notifyIfKingInCheck(context.getCurrentPlayerColor());

        context.setGameState(GameStateFactory.createGameStateNoPieceSelected(board,plies,legalSquares,context));
        gameStateObservers.forEach(context::addGameStateObserver);
    }

    private void move(Square selectedSquare, Square targetSquare) {
        makeSpecialMoves(selectedSquare, targetSquare);
        makeMoves(selectedSquare, targetSquare);
    }

    /**
     * Checks if any special moves are possible and if so, performs the necessary actions
     *
     * @param selectedSquare the currently selected square
     * @param targetSquare the square being moved to
     */
    private void makeSpecialMoves(Square selectedSquare, Square targetSquare) {
        if (!board.isAPieceOnSquare(selectedSquare)) return;

        //castling
        if (targetSquare.getSquareType() == CASTLING) {
            if (targetSquare.getX() > selectedSquare.getX()) {
                makeMoves(new Square(targetSquare.getX() + 1, targetSquare.getY()), new Square(targetSquare.getX() - 1, targetSquare.getY()));
            } else if (targetSquare.getX() < selectedSquare.getX()) {
                makeMoves(new Square(targetSquare.getX() - 2, targetSquare.getY()), new Square(targetSquare.getX() + 1, targetSquare.getY()));
            }
        }
        //en passant
        if (targetSquare.getSquareType() == EN_PASSANT) {
            if (board.pieceOnSquareColorEquals(selectedSquare, WHITE)) {
                takePiece(new Square(targetSquare.getX(), targetSquare.getY() + 1));
            } else if (board.pieceOnSquareColorEquals(selectedSquare, BLACK)) {
                takePiece(new Square(targetSquare.getX(), targetSquare.getY() - 1));
            }
        }
    }

    /**
     * Moves the marked piece to the clicked square
     * <p>
     */
    private void makeMoves(Square moveFrom, Square moveTo) {
        if (board.isSquareContainsAPiece(moveTo)) {
            takePiece(moveTo);
        }
        board.markPieceOnSquareHasMoved(moveFrom);
        board.placePieceOnSquare(moveTo, board.getPieceOnSquare(moveFrom));
        board.removePieceFromSquare(moveFrom);
    }


    /**
     * take a piece on a square and put it in dead pieces
     *
     * @param squareWithPieceToTake the square that has the piece to be taken
     */
    private void takePiece(Square squareWithPieceToTake) {
        takenPiece = board.removePieceFromSquare(squareWithPieceToTake);
        board.getDeadPieces().add(takenPiece);
        notifyDrawDeadPieces();
    }

    private void clearAndDrawLegalMoves() {
        legalSquares.clear();
        notifyDrawLegalMoves();
    }

    /**
     * Checks if the king of the inputted color is in check and notifies if it is
     *
     * @param kingColor the color of the king to check
     */
    private void notifyIfKingInCheck(ChessColor kingColor) {
        ChessColor opponentColor = (kingColor == WHITE) ? BLACK : WHITE;
        Square kingSquare = board.fetchKingSquare(kingColor);

        if (MovementLogicUtil.isKingInCheck(board, kingSquare, opponentColor))
            notifyKingInCheck(kingSquare.getX(), kingSquare.getY());
    }

    /**
     * checks if the king has been taken by looking at the dead pieces list
     *
     * @return true if king has been taken
     */
    private boolean checkKingTaken() {
        for (IPiece p : board.getDeadPieces()) {
            if (p.getPieceType().equals(KING)) return true;
        }
        return false;
    }

    /**
     * Checks if pawn a pawn is in a position to be promoted
     *
     * @param targetSquare square to be checked
     * @return true if piece cam be promoted
     */
    private boolean checkPawnPromotion(Square targetSquare) {
        if (targetSquare.getSquareType() == PROMOTION) {
            notifyPawnPromotion();
            return true;
        }
        return false;
    }

    /**
     * adds the move(ply) made to the list of plies
     *
     * @param selectedSquare the currently selected square
     * @param targetSquare the square being moved to
     */
    private void addMoveToPlies(Square selectedSquare, Square targetSquare) {
        Ply ply = new Ply(context.getCurrentPlayerName(), selectedSquare, targetSquare, board.getPieceOnSquare(targetSquare), takenPiece, board.getBoardSnapShot());
        plies.add(ply);
    }

    /**
     * returns a square if it matches the coordinates inputted
     * should never be null so throws exception if null
     *
     * @param x the horizontal coordinate to match
     * @param y the vertical coordinate to match
     * @return the matching square
     */
    private Square getLegalSquareByCoordinates(int x, int y) {
        for (Square s : legalSquares) {
            if (s.getX() == x && s.getY() == y)
                return s;
        }
        throw new NoSuchElementException("No legal square with matching coordinates found");
    }

    private void notifyPawnPromotion() {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifyPawnPromotionSetup();
        }
    }

    private void notifyDrawPieces() {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifyDrawPieces();
        }
    }

    private void notifyDrawDeadPieces() {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifyDrawDeadPieces();
        }
    }

    private void notifySwitchPlayer() {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifySwitchPlayer();
        }
    }

    private void notifyDrawLegalMoves() {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifyDrawLegalMoves();
        }
    }

    private void notifyKingInCheck(int x, int y) {
        for (GameStateObserver gameStateObserver : gameStateObservers) {
            gameStateObserver.notifyKingInCheck(x, y);
        }
    }


    @Override
    public String getGameStatus() {
        return "Game ongoing";
    }

    @Override
    public boolean isGameOngoing() {
        return true;
    }

    @Override
    public void addGameStateObserver(GameStateObserver gameStateObserver) {
        gameStateObservers.add(gameStateObserver);
    }
}
