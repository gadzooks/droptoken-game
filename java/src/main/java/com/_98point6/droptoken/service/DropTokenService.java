package com._98point6.droptoken.service;

import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// GameBoard 2 D array, 2 players info, who goes next, state of game, moves
// Games -> hashmap key board id -> value reference to board game
// players
// moves need to be tracked and in order -> array list ?
// state pattern to keep track of state change when a move is made ?
// enums for various types
//

public class DropTokenService {
    private static final Map<UUID, GameBoard> games = new HashMap<>();

    public static String createGame(List<String> players) {
        GameBoard board = new GameBoard(players);
        UUID id = board.getId();
        games.put(id, board);
        return id.toString();
    }

    public static List<String> getGames() {
        return games.
                values().
                stream().
                map(GameBoard::getId).
                map(UUID::toString).
                collect(Collectors.toList());
    }

    public static String getGameStatus(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId
        return game.getGameStatus();
    }

    public static String nextMove(String gameId, String playerId, Integer column) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId

        return game.postMove(playerId, column);
    }

    public static void quitGame(String gameId, String playerId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        // TODO check for invalid gameId

        game.quit(playerId);
    }

    public static GameBoard getGame(String gameId) {
        return games.get(gameId);
    }

    public static GameStatus getGameState(String gameId) {
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

    public static List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) {
        // TODO validate from, until, gameId

        GameBoard game = games.get(UUID.fromString(gameId));
        return game.getMoves(from, until);
    }
}
