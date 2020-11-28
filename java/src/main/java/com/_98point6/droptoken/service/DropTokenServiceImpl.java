package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;

import java.util.*;
import java.util.stream.Collectors;

public class DropTokenServiceImpl implements DropTokenService {
    private  final Map<UUID, GameBoard> games = new HashMap<>();

    @Override
    public Optional<GameBoard> createGame(List<String> players, int rows, int columns) {
        System.out.println(players);
        try {
            GameBoard board = new GameBoardImpl(players, rows, columns);
            UUID id = board.getId();
            games.put(id, board);
            return Optional.of(board);
        } catch(IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public  List<String> getGames() {
        return games.
                values().
                stream().
                map(GameBoard::getId).
                map(UUID::toString).
                collect(Collectors.toList());
    }

    @Override
    public GameState getGameStatus(String gameId) {
        GameBoard game = findById(gameId);
        return game.getGameState();
    }

    @Override
    public String nextMove(String gameId, String playerId, int column) {
        GameBoard game = findById(gameId);
        return game.postMove(playerId, column);
    }

    @Override
    public GameBoard findById(String gameId) {
        GameBoard game = games.get(UUID.fromString(gameId));
        if (game == null) {
            // TODO throw custom error
            throw new IllegalArgumentException(
                    String.format("invalid gameId %s specified", gameId));
        }

        return game;
    }

    @Override
    public void quitGame(String gameId, String playerId) {
        GameBoard game = findById(gameId);
        game.quit(playerId);
    }

    @Override
    public GameBoard getGame(String gameId) {
        return findById(gameId);
    }

    @Override
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

    @Override
    public  List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until) {
        // TODO validate from, until, gameId
        GameBoard game = findById(gameId);

        return game.getMoves(from, until);
    }
}
