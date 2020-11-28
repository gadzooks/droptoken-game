package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;

import java.util.*;
import java.util.stream.Collectors;

public class DropTokenServiceImpl implements DropTokenService {
    private  final Map<UUID, GameBoard> games = new HashMap<>();

    public  Optional<GameBoard> createGame(List<String> players, Integer columns, Integer rows) {
        try {
            GameBoard board = new GameBoardImpl(players, columns, rows);
            UUID id = board.getId();
            games.put(id, board);
            return Optional.of(board);
        } catch(IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public  List<String> getGames() {
        return games.
                values().
                stream().
                map(GameBoard::getId).
                map(UUID::toString).
                collect(Collectors.toList());
    }

    public GameState getGameStatus(String gameId) {
        GameBoard game = findById(gameId);
        return game.getGameState();
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
        GameBoard game;
        try {
            game = games.get(UUID.fromString(gameId));
            if(game == null) {
                return Optional.empty();
            }

            GameStatusResponse.Builder builder = new GameStatusResponse.Builder();
            builder.state(game.getGameState().toString());
            builder.players(game.getPlayers());
            builder.winner(game.getWinner());
            // TODO
            //builder.moves(game.getMoves());

            return Optional.ofNullable(builder.build());
        } catch(IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public  List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) {
        // TODO validate from, until, gameId
        GameBoard game = findById(gameId);

        return game.getMoves(from, until);
    }
}
