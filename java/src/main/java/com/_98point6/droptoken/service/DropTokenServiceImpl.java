package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.exception.DropTokenException;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;
import org.eclipse.jetty.server.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DropTokenServiceImpl implements DropTokenService {
    private final Map<UUID, GameBoard> games = new HashMap<>();

    @Override
    public GameBoard createGame(final List<String> players, final int rows, final int columns)
            throws DropTokenException {
        try {
            GameBoard board = new GameBoardImpl(players, rows, columns);
            UUID id = board.getId();
            games.put(id, board);
            return board;
        } catch(GameBoard.MalformedGameRequestException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public List<String> getGames() {
        return games.
                values().
                stream().
                map(GameBoard::getId).
                map(UUID::toString).
                collect(Collectors.toList());
    }

    @Override
    public GameState getGameStatus(String gameId) throws DropTokenException {
        GameBoard game = findById(gameId);
        return game.getGameState();
    }

    @Override
    public String nextMove(String gameId, String playerId, int column) throws DropTokenException {
        GameBoard game = findById(gameId);
        try {
            return game.postMove(playerId, column);
        } catch (GameBoard.InvalidGameOrPlayerException e) {
            throw new DropTokenException(Response.SC_NOT_FOUND, e.getMessage(), e);
        } catch (GameBoard.PlayerOutOfTurnException e) {
            throw new DropTokenException(Response.SC_CONFLICT, e.getMessage(), e);
        } catch (GameBoard.MalformedInputException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        } catch (GameBoard.IllegalMoveException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public GameBoard findById(String gameId) throws DropTokenException {
        GameBoard game = games.get(UUID.fromString(gameId));
        if (game == null) {
            throw new DropTokenException(Response.SC_NOT_FOUND, "Games/moves not found.");
        }

        return game;
    }

    @Override
    public void quitGame(String gameId, String playerId) throws DropTokenException {
        GameBoard game = findById(gameId);
        game.quit(playerId);
    }

    @Override
    public GameBoard getGame(String gameId) throws DropTokenException {
        return findById(gameId);
    }

    @Override
    public GameStatusResponse getGameState(String gameId) throws DropTokenException {
        GameBoard game = findById(gameId);

        GameStatusResponse.Builder builder = new GameStatusResponse.Builder();
        builder.state(game.getStatus());
        builder.players(game.getPlayers());
        if(game.getWinner() != null)
            builder.winner(game.getWinner());
        builder.moves(game.getTotalMoves());

        return builder.build();
    }

    @Override
    public  List<com._98point6.droptoken.dto.game.Move> getMoves(String gameId, int from, int until)
            throws DropTokenException {
        GameBoard game = findById(gameId);

        try {
            return game.getMoves(from, until);
        } catch (GameBoard.MalformedInputException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public List<com._98point6.droptoken.dto.game.Move> getAllMoves(String gameId) throws DropTokenException {
        GameBoard game = findById(gameId);
        try {
            return game.getMoves(0, game.getTotalMoves());
        } catch (GameBoard.MalformedInputException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }
}
