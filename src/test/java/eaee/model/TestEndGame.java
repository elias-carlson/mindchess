package eaee.model;

import chess.model.ChessFacade;
import chess.model.Movement;
import chess.model.Piece;
import chess.model.PieceType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TestEndGame {
    ChessFacade model;
    Map<Point, Piece> boardMap = new HashMap<>();
    Movement movement = new Movement();

    @Before
    public void init() {
        model = new ChessFacade();
        model.createNewGame();
        boardMap = model.getGame().getBoard().getBoardMap();
        movement.setBoardMap(boardMap);
    }

    //Test taking the king
    //gamelist = 0 after taking a king
    @Test
    public void testCheckKingTaken(){
        model.handleBoardClick(2,6);
        model.handleBoardClick(2,4);

        model.handleBoardClick(3,1);
        model.handleBoardClick(3,3);

        model.handleBoardClick(4,7);
        model.handleBoardClick(0,4);

        model.handleBoardClick(3,3);
        model.handleBoardClick(2,4);

        //white queen takes takes black king
        model.handleBoardClick(0,4);
        model.handleBoardClick(4,0);
        assertEquals(1, model.getGameList().size());
    }

    //Test WhitePlayerWin


    //Test time running out method?




    //future tests for draw and surrender
}