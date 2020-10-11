package chess.model;

import chess.model.GameState.GameState;

import java.awt.Point;
import java.util.List;
import java.util.Map;

public interface IGameContext {
    void setGameState(GameState gameState);
    Player getCurrentPlayer();
    List<Point> getLegalPoints();
    List<Piece> getDeadPieces();
    List<Ply> getPlies();
    Map<Point,Piece> getBoardMap();
    Movement getMovement();
    void switchPlayer();
    void notifyDrawPieces();
    void notifyDrawDeadPieces();
    void notifyDrawLegalMoves();
    void notifyPawnPromotion(ChessColor chessColor);
}