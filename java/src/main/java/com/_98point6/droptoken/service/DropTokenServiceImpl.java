package com._98point6.droptoken.service;

import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;

import java.util.*;
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

    public String getGameStatus(String gameId) {
        GameBoard game = findById(gameId);
        return game.getGameStatus();
    }

    public String nextMove(String gameId, String playerId, Integer column) {
        GameBoard game = findById(gameId);
        return game.postMove(playerId, column);
    }

    public GameBoard findById(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        if (game == null) {
            // TODO throw custom error
            throw new IllegalArgumentException(
                    String.format("invalid gameId %s specified", gameId));
        }

        return game;
    }

    public void quitGame(String gameId, String playerId) {
        GameBoard game = findById(gameId);
        game.quit(playerId);
    }

    public GameBoard getGame(String gameId) {
        return findById(gameId);
    }

    public Optional<GameStatusResponse> getGameState(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        if(game == null) {
            return Optional.empty();
        }

        GameStatusResponse.Builder builder = new GameStatusResponse.Builder();
        builder.state(game.getGameStatus());
        builder.players(game.getPlayers());
        builder.winner(game.getWinner());
        // TODO
        //builder.moves(game.getMoves());

        return Optional.ofNullable(builder.build());
    }

    public  List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) {
        // TODO validate from, until, gameId
        GameBoard game = findById(gameId);

        return game.getMoves(from, until);
    }
}
