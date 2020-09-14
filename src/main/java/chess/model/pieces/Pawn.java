package chess.model.pieces;

import chess.model.Color;
import chess.model.Square;

public class Pawn extends Piece {

    public Pawn(Square position, boolean isActive, Color color) {
        super(position, isActive, color);
        whiteImageURL = "/chessPieces/white_pawn.png";
        blackImageURL = "/chessPieces/black_pawn.png";
    }
}