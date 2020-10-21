package chess.model;

import chess.model.pieces.IPiece;

import java.util.ArrayList;
import java.util.List;

public class GameStateNoPieceSelected implements GameState {
    private IGameContext context;
    private List<GameStateObserver> gameStateObservers = new ArrayList<>();
    private List<Square> legalSquares;
    private List<Ply> plies;
    private Board board;
    GameStateNoPieceSelected(Board board, List<Ply> plies, List<Square> legalSquares, IGameContext context) {
        this.board = board;
        this.legalSquares = legalSquares;
        this.plies = plies;
        this.context = context;
    }

    @Override
    public void handleInput(int x, int y) {
        Square selectedSquare = new Square(x,y);
        if(SquareIsAPiece(selectedSquare) && isPieceMyColor(selectedSquare)) {
            fetchLegalMoves(selectedSquare);
            if(legalSquares.size() == 0) return;
            notifyDrawLegalMoves();
            context.setGameState(GameStateFactory.createGameStatePieceSelected(selectedSquare,board,plies,legalSquares,context));
            gameStateObservers.forEach(gameStateObserver -> context.getGameState().addGameStateObserver(gameStateObserver));
        }
    }

    /**
     * Adds all legal squares the marked piece can move to to the legalSquares list
     */
    private void fetchLegalMoves(Square selectedSquare) {
        IPiece pieceToCheck = board.fetchPieceOnSquare(selectedSquare);
        legalSquares.addAll(pieceToCheck.getMoveDelegate().fetchMoves(board, selectedSquare, pieceToCheck.getHasMoved(), true));
        legalSquares.addAll(getEnPassantSquares(selectedSquare));
    }

    private List<Square> getEnPassantSquares(Square selectedSquare) {
        List<Square> enPassantPoints = new ArrayList<>();
        if (plies.size() == 0) return enPassantPoints;

        Ply lastPly = plies.get(plies.size() - 1);

        return MovementLogicUtil.getEnPassantSquares(lastPly, selectedSquare, board);
    }

    private boolean SquareIsAPiece(Square square){
        if(board.isSquareAPiece(square)) return true;
        return false;
    }

    private boolean isPieceMyColor(Square square){
        //return context.getBoard().getBoardMap().get(square).getColor() == context.getCurrentPlayerColor();
        return board.pieceOnSquareColorEquals(square,context.getCurrentPlayerColor());
    }

    private void notifyDrawLegalMoves(){
        for (GameStateObserver gameStateObserver: gameStateObservers) {
            gameStateObserver.notifyDrawLegalMoves();
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
