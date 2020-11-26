package com._98point6.droptoken.model.game;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private static final List<String> PLAYERS = Arrays.asList("p1", "p2");
    private static final String P1 = "p1";
    private static final String P2 = "p2";

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void test_ensurePlayersAlternate() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);
        game.postMove(P1,0);

        assertThrows(IllegalArgumentException.class, () -> {
            game.postMove(P1,0);
        });
    }

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void test_ensurePlayersCannotPlayOnceGameIsOver() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);
        game.quit(P1);

        // THEN
        assertThrows(IllegalArgumentException.class, () -> {
            //WHEN
            game.postMove(P1,0);
        });

        // THEN
        assertThrows(IllegalArgumentException.class, () -> {
            //WHEN
            game.postMove(P2,0);
        });
    }

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void testGetGameStatus_withSimpleGame() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);
        assertEquals("IN_PROGRESS", game.getGameStatus());
        assertNull(game.getWinner());
        assertEquals(0, game.getTotalMoves());

        // WHEN
        for (int i = 0; i < 3; i++) {
            game.postMove(P1, 0);
            game.postMove(P2, 1);
            assertEquals("IN_PROGRESS", game.getGameStatus());
            assertNull(game.getWinner());
            assertEquals(2*(i + 1), game.getTotalMoves());
        }

        // winning move
        game.postMove(P1, 0);

        // THEN
        assertEquals("DONE", game.getGameStatus());
        assertEquals(P1, game.getWinner());
        assertEquals(7, game.getTotalMoves());
    }

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void testGetGame_withPlayerQuitting() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);
        assertEquals("IN_PROGRESS", game.getGameStatus());
        assertNull(game.getWinner());

        // WHEN
        game.postMove(P2, 0);
        // can quit anytime, even out of turn
        game.quit(P2);
        assertEquals("DONE", game.getGameStatus());
        assertEquals(P1, game.getWinner());
    }

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void testGetGame_withPlayerQuitting_andPlayingAgain() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);

        // WHEN
        game.quit(P2);

        // THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            //WHEN
            game.postMove(P1,0);
        });

        String expectedMessage = "Game is over. No more moves allowed";
        assertEquals(expectedMessage, exception.getMessage());

    }

        @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void testGetGame_withTooManyInOneColumn() {
        // GIVEN
        int sameColumn = 3;
        GameBoard game = new GameBoard(PLAYERS);
        game.postMove(P2, sameColumn);
        game.postMove(P1, sameColumn);
        game.postMove(P2, sameColumn);
        game.postMove(P1, sameColumn);

        // THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            //WHEN
            game.postMove(P2, sameColumn);
        });

        String expectedMessage = "No more slots available for column 3";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    // simple case where p1 will drop 4 tokens into 1st column and win
    public void testGetGameStatus_withDrawnGame() {
        // GIVEN
        GameBoard game = new GameBoard(PLAYERS);
        assertEquals("IN_PROGRESS", game.getGameStatus());
        assertNull(game.getWinner());
        assertEquals(0, game.getTotalMoves());

        // WHEN
        for (int i = 0; i < 3; i++) {
            game.postMove(P1, 0);
            game.postMove(P2, 1);
            assertEquals("IN_PROGRESS", game.getGameStatus());
            assertNull(game.getWinner());
            assertEquals(2*(i + 1), game.getTotalMoves());
        }

        // winnning move
        game.postMove(P1, 0);
        assertEquals("DONE", game.getGameStatus());
        assertEquals(P1, game.getWinner());
        assertEquals(7, game.getTotalMoves());
    }

}