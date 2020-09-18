package chess.controller;

import chess.model.Chess;
import chess.model.Color;
import chess.model.Piece;
import chess.model.Square;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static chess.model.Color.*;

public class ImageHandler {
    private List<ImageView> pieceImages = new ArrayList<>();
    private Map<Piece, ImageView> pieceImageViewMap = new HashMap<>();
    private Chess model = Chess.getInstance();

    public List<ImageView> getPieceImages() {
        return pieceImages;
    }

    public List<ImageView> fetchPieceImages() {
        for (Piece p : model.getBoard().getPieces()) {
            String imageURL = "";
            switch (p.getPieceType()) {
                case ROOK:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_rook.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_rook.png";
                    }
                    break;
                case BISHOP:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_bishop.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_bishop.png";
                    }
                    break;
                case KNIGHT:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_knight.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_knight.png";
                    }
                    break;
                case QUEEN:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_queen.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_queen.png";
                    }
                    break;
                case KING:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_king.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_king.png";
                    }
                    break;
                case PAWN:
                    if (p.getColor().equals(BLACK)) {
                        imageURL = "/chesspieces/black_pawn.png";
                    } else if(p.getColor().equals(WHITE)) {
                        imageURL = "/chesspieces/white_pawn.png";
                    }
                    break;
            }
            ImageView pieceImage = new ImageView();
            pieceImage.setImage(new Image(getClass().getResourceAsStream(imageURL)));
            pieceImage.setX(p.getSquare().getCoordinatesX() * 75 + 7.5);
            pieceImage.setY(p.getSquare().getCoordinatesY() * 75 + 7.5);
            pieceImages.add(pieceImage);
            pieceImageViewMap.put(p, pieceImage);
        }
        return pieceImages;
    }


    public void updateImageCoordinates() {
        for(Map.Entry<Piece, ImageView> entry : pieceImageViewMap.entrySet()) {
            entry.getValue().setX(entry.getKey().getSquare().getCoordinatesX() * 75 + 7.5);
            entry.getValue().setY(entry.getKey().getSquare().getCoordinatesY() * 75 + 7.5);
        }
    }

    /**temp
     *
     * (does) finds and returns a list of images on all squares
     *
     * (should) finds and returns a list of images for the squares a piece is allowed to move to
     *
     * @return returns a list of images on all squares
     */
    List<ImageView> fetchLegalMoveImages() {
        List<ImageView> imageViews = new ArrayList<>();
        for (Square s : model.getBoard().getMockLegalSquares()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(getClass().getResourceAsStream("/legalMove.png")));
            imageView.setX(s.getCoordinatesX() * 75 + 30);
            imageView.setY(s.getCoordinatesY() * 75 + 30);
            imageView.setFitHeight(15);
            imageView.setFitWidth(15);
            imageViews.add(imageView);
        }
        return imageViews;
    }
}