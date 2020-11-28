package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;

import java.util.List;
import java.util.Optional;

public interface DropTokenService {
    Optional<GameBoard> createGame(List<String> players, int rows, int columns);
    List<String> getGames();
    GameState getGameStatus(String gameId);
    String nextMove(String gameId, String playerId, int column);
    GameBoard findById(String gameId);
    void quitGame(String gameId, String playerId);
    GameBoard getGame(String gameId);
    Optional<GameStatusResponse> getGameState(String gameId);
    List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until);
}
