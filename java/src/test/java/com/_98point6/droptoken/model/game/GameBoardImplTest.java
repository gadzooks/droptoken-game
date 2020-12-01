package com._98point6.droptoken.model.game;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardImplTest {
    private static final List<String> PLAYERS = Arrays.asList("p1", "p2");
    private static final String P1 = "p1";
    private static final String P2 = "p2";
    private static final int columns = 4;
    private static final int rows = 4;

    @SneakyThrows
    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void test_ensurePlayersAlternate() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        game.postMove(P1,0);

        assertThrows(GameBoard.PlayerOutOfTurnException.class, () -> {
            game.postMove(P1,0);
        });
    }

    @SneakyThrows
    @Test
    public void test_ensurePlayersCannotPlayOnceGameIsOver() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        game.quit(P1);

        // THEN
        assertThrows(GameBoard.MalformedInputException.class, () -> {
            //WHEN
            game.postMove(P1,0);
        });

        // THEN
        assertThrows(GameBoard.MalformedInputException.class, () -> {
            //WHEN
            game.postMove(P2,0);
        });
    }

    @SneakyThrows
    @Test
    public void testGetGameStatus_withSimpleGame() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        assertEquals("IN_PROGRESS", game.getStatus());
        assertNull(game.getWinner());
        assertEquals(0, game.getTotalMoves());

        // WHEN
        for (int i = 0; i < 3; i++) {
            game.postMove(P1, 0);
            game.postMove(P2, 1);
            assertEquals("IN_PROGRESS", game.getStatus());
            assertNull(game.getWinner());
            assertEquals(2*(i + 1), game.getTotalMoves());
        }

        // winning move
        game.postMove(P1, 0);

        // THEN
        assertEquals("DONE", game.getStatus());
        assertEquals(P1, game.getWinner());
        assertEquals(7, game.getTotalMoves());
    }

    @SneakyThrows
    @Test
    public void testGetGame_withPlayerQuitting() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        assertEquals("IN_PROGRESS", game.getStatus());
        assertNull(game.getWinner());

        // WHEN
        game.postMove(P2, 0);
        // can quit anytime, even out of turn
        game.quit(P2);
        assertEquals("DONE", game.getStatus());
        assertEquals(P1, game.getWinner());
    }

    @SneakyThrows
    @Test
    public void testGetGame_withPlayerQuitting_andPlayingAgain() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);

        // WHEN
        game.quit(P2);

        // THEN
        assertThrows(GameBoard.MalformedInputException.class, () -> {
            //WHEN
            game.postMove(P1,0);
        });

    }

    @SneakyThrows
    @Test
    public void testGetGame_withTooManyInOneColumn() {
        // GIVEN
        int sameColumn = 3;
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        game.postMove(P2, sameColumn);
        game.postMove(P1, sameColumn);
        game.postMove(P2, sameColumn);
        game.postMove(P1, sameColumn);

        // THEN
        assertThrows(GameBoard.IllegalMoveException.class, () -> {
            //WHEN
            game.postMove(P2, sameColumn);
        });
    }

    @SneakyThrows
    @Test
    public void testGetGameStatus_withColumnWin() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);

        // WHEN
        game.postMove(P1, 0);
        game.postMove(P2, 1);
        game.postMove(P1, 0);
        game.postMove(P2, 1);
        game.postMove(P1, 0);
        game.postMove(P2, 1);

        // random moves
        game.postMove(P1,2);
        game.postMove(P2,2);
        game.postMove(P1,2);
        game.postMove(P2,2);

        // winning moves
        game.postMove(P1, 0);

        // THEN
        assertEquals("DONE", game.getStatus());
        assertEquals(P1, game.getWinner());
    }

    @SneakyThrows
    @Test
    public void testGetGameStatus_withRowWin() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);

        game.postMove(P1, 0);
        game.postMove(P2, 0);
        game.postMove(P1, 1);
        game.postMove(P2, 1);
        game.postMove(P1, 2);
        game.postMove(P2, 2);

        // WHEN
        // winning moves
        game.postMove(P1, 3);

        // THEN
        assertEquals("DONE", game.getStatus());
        assertEquals(P1, game.getWinner());
    }

    @SneakyThrows
    @Test
    public void testGetGame_withDraw() {
        // GIVEN
        GameBoard game = new GameBoardImpl(PLAYERS, columns, rows);
        assertEquals("IN_PROGRESS", game.getStatus());
        assertNull(game.getWinner());

        // WHEN
        // fill up row 0
        game.postMove(P1, 0);
        game.postMove(P2, 1);
        game.postMove(P1, 2);
        game.postMove(P2, 3);

        // fill up row 1
        game.postMove(P1, 0);
        game.postMove(P2, 1);
        game.postMove(P1, 2);
        game.postMove(P2, 3);

        // fill up row 2
        game.postMove(P1, 3);
        game.postMove(P2, 2);
        game.postMove(P1, 1);
        game.postMove(P2, 0);

        // fill up row 2
        game.postMove(P1, 3);
        game.postMove(P2, 2);
        game.postMove(P1, 1);
        game.postMove(P2, 0);

        assertEquals("DONE", game.getStatus());
        assertNull(game.getWinner());
    }

}