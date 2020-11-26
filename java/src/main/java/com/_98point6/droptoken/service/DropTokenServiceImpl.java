package com._98point6.droptoken.service;

import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DropTokenServiceImpl implements DropTokenService {
    private  final Map<UUID, GameBoard> games = new HashMap<>();

    public  String createGame(List<String> players) {
        GameBoard board = new GameBoard(players);
        UUID id = board.getId();
        games.put(id, board);
        return id.toString();
    }

    public  List<String> getGames() {
        return games.
                values().
                stream().
                map(GameBoard::getId).
                map(UUID::toString).
                collect(Collectors.toList());
    }

    public  String getGameStatus(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId
        return game.getGameStatus();
    }

    public  String nextMove(String gameId, String playerId, Integer column) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId

        return game.postMove(playerId, column);
    }

    public  void quitGame(String gameId, String playerId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId

        game.quit(playerId);
    }

    public GameBoard getGame(String gameId) {
        return games.get(gameId);
    }

    public GameStatus getGameState(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        if(game != null) {
            GameStatus gameStatus = new GameStatus(
                    game.getPlayers(),
                    game.getGameStatus(),
                    game.getWinner(),
                    game.getTotalMoves()
            );

            return gameStatus;
        }

        return null;
    }

    public  List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) {
        // TODO validate from, until, gameId

        GameBoard game = games.get(UUID.fromString(gameId));
        return game.getMoves(from, until);
    }
}
