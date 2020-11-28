package com._98point6.droptoken.model.game;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.dto.game.Move;

import java.util.List;
import java.util.UUID;

public interface GameBoard {
    String getStatus();
    UUID getId();
    int getTotalMoves();
    List<String> getPlayers();
    GameState getGameState();
    String postMove(String playerId, int column);
    String getWinner();
    // player can quit any time they want
    void quit(String playerId);
    List<Move> getMoves(int from, int to);

    String INVALID_GAME_OR_PLAYER = "Game not found or player is not a part of it.";
    String MALFORMED_INPUT = "Malformed input. Illegal move.";
    String MALFORMED_GAME_REQUEST = "Malformed request.";
    String PLAYER_OUT_OF_TURN = "Player tried to post when it's not their turn.";

    class IllegalMoveException extends RuntimeException {
        public IllegalMoveException() {
            super(MALFORMED_INPUT);
        }
    }

    class MalformedInputException extends RuntimeException {
        public MalformedInputException() {
            super(MALFORMED_INPUT);
        }
    }

    class MalformedGameRequestException extends RuntimeException {
        public MalformedGameRequestException() {
            super(MALFORMED_GAME_REQUEST);
        }
    }

    class InvalidGameOrPlayerException extends RuntimeException {
        public InvalidGameOrPlayerException() {
            super(INVALID_GAME_OR_PLAYER);
        }
    }

    class PlayerOutOfTurnException extends RuntimeException {
        public PlayerOutOfTurnException() {
            super(PLAYER_OUT_OF_TURN);
        }
    }

}
