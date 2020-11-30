package com._98point6.droptoken.service;

import com._98point6.droptoken.dto.game.GameState;
import com._98point6.droptoken.exception.DropTokenException;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;
import org.eclipse.jetty.server.Response;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ThreadSafe
public class DropTokenServiceImpl implements DropTokenService {
    // if multiple instances of Service are created they all need to share the same games map so
    // it needs to be declared as static
    // volatile : we dont want threads caching games since games can be changed by other threads.
    private static volatile Map<UUID, GameBoard> games = new ConcurrentHashMap<>();

    private static synchronized void addGameSynchronized(UUID id, GameBoard game) {
        games.put(id, game);
    }

    private static synchronized GameBoard getGameSynchronized(UUID id) {
        return games.get(id);
    }

    private static synchronized Map<UUID, GameBoard> getGamesSynchronized() {
        return games;
    }

    @Override
    public GameBoard createGame(final List<String> players, final int rows, final int columns)
            throws DropTokenException {
        try {
            final GameBoard board = new GameBoardImpl(players, rows, columns);
            final UUID id = board.getId();
            addGameSynchronized(id, board);
            return board;
        } catch(GameBoard.MalformedGameRequestException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public List<String> getGames() {
        return getGamesSynchronized().
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
            // TODO needs to be synchronized
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
        // catch IllegalArgumentException in fromString
        UUID id = UUID.fromString(gameId);
        GameBoard game = getGameSynchronized(id);
        if (game == null) {
            throw new DropTokenException(Response.SC_NOT_FOUND, "Games/moves not found.");
        }

        return game;
    }

    @Override
    public void quitGame(String gameId, String playerId) throws DropTokenException {
        GameBoard game = findById(gameId);
        // TODO needs to be synchronized
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
        // synchronize all this against the game ?
        builder.state(game.getStatus());
        builder.players(game.getPlayers());
        // TODO needs to be synchronized
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
            // TODO needs to be synchronized
            return game.getMoves(from, until);
        } catch (GameBoard.MalformedInputException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public List<com._98point6.droptoken.dto.game.Move> getAllMoves(String gameId) throws DropTokenException {
        GameBoard game = findById(gameId);
        try {
            // TODO needs to be synchronized
            return game.getMoves(0, game.getTotalMoves());
        } catch (GameBoard.MalformedInputException e) {
            throw new DropTokenException(Response.SC_BAD_REQUEST, e.getMessage(), e);
        }
    }
}
