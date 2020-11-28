package com._98point6.droptoken.model.game;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.dto.game.Move;

import java.util.List;
import java.util.UUID;

public interface GameBoard {
    // useful for testing
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

}
