package mindchess.model;

import mindchess.model.pieces.IPiece;
import mindchess.model.pieces.PieceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStatePawnPromotion implements GameState {

    private Map<Square, PieceType> promotionPieces = new HashMap<>();

    private IGameContext context;
    private Square selectedSquare;
    private List<GameStateObserver> gameStateObservers = new ArrayList<>();;
    private List<Square> legalSquares;
    private List<Ply> plies;
    private Board board;

    GameStatePawnPromotion(Square selectedSquare, Board board, List<Ply> plies, List<Square> legalSquares, IGameContext context) {
        this.selectedSquare = selectedSquare;
        this.board = board;
        this.legalSquares = legalSquares;
        this.plies = plies;
        this.context = context;
    }

    @Override
    public void handleInput(int x, int y) {
        initPromotionPieces();
        Square selectedPromotion = new Square(x,y);
        if(promotionPieces.containsKey(selectedPromotion)){
            promote(selectedSquare,selectedPromotion);
            notifySwitchPlayer();
            notifyDrawPieces();
            context.setGameState(GameStateFactory.createGameStateNoPieceSelected(board,plies,legalSquares,context));
            gameStateObservers.forEach(gameStateObserver -> context.addGameStateObserver(gameStateObserver));
        }
    }

    private void initPromotionPieces(){
        promotionPieces.put(new Square(20,0), PieceType.QUEEN);
        promotionPieces.put(new Square(21,0), PieceType.KNIGHT);
        promotionPieces.put(new Square(22,0), PieceType.ROOK);
        promotionPieces.put(new Square(23,0), PieceType.BISHOP);
    }

    private void promote(Square selectedSquare, Square selectedPromotion){
        IPiece piece = null;
        try {
            piece = PieceFactory.createPiece(promotionPieces.get(selectedPromotion), context.getCurrentPlayerColor());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Piece Name");
        }

        board.getBoardMap().put(selectedSquare, piece);
    }

    private void notifySwitchPlayer(){
        for (GameStateObserver gameStateObserver: gameStateObservers) {
            gameStateObserver.notifySwitchPlayer();
        }
    }

    private void notifyDrawPieces(){
        for (GameStateObserver gameStateObserver: gameStateObservers) {
            gameStateObserver.notifyDrawPieces();
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