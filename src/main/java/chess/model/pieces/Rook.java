package chess.model.pieces;

import chess.model.Color;
import chess.model.Square;

public class Rook extends Piece {
    public Rook(Square position, boolean isActive, Color color) {
        super(position, isActive, color);
        whiteImageURL = "/chessPieces/white_rook.png";
        blackImageURL = "/chessPieces/black_rook.png";
    }
}