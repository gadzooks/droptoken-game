package com._98point6.droptoken.model.game;

import java.util.List;
import java.util.UUID;

public interface GameBoard {
    UUID getId();
    int getTotalMoves();
    List<String> getPlayers();
    String getGameStatus();
    String postMove(String playerId, int column);
    String getWinner();
    // player can quit any time they want
    void quit(String playerId);
    List<com._98point6.droptoken.dto.game.Move> getMoves(int from, int to);

}
