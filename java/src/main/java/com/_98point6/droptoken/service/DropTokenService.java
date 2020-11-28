package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.exception.DropTokenException;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;

import java.util.List;

public interface DropTokenService {
    GameBoard createGame(List<String> players, int rows, int columns) throws DropTokenException;
    List<String> getGames();
    GameState getGameStatus(String gameId) throws DropTokenException;
    String nextMove(String gameId, String playerId, int column) throws DropTokenException;
    GameBoard findById(String gameId) throws DropTokenException;
    void quitGame(String gameId, String playerId) throws DropTokenException;
    GameBoard getGame(String gameId) throws DropTokenException;
    GameStatusResponse getGameState(String gameId) throws DropTokenException;
    List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) throws DropTokenException;
}
