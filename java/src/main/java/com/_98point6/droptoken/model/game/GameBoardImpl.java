package com._98point6.droptoken.model.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoardImpl implements GameBoard{
    private static final int LENGTH = 4;
    private static final int TOTAL_PLAYERS = 2;

    private final UUID id = UUID.randomUUID();
    private final String[] players = new String[TOTAL_PLAYERS];
    private final String[][] matrix = new String[LENGTH][LENGTH];
    private final List<Move> moves = new ArrayList<>();
    @Setter(AccessLevel.PRIVATE)
    private GameBoardImpl.GameState status = GameBoardImpl.GameState.IN_PROGRESS;
    @Getter
    private String winner = null;
    private String nextPlayer = null;

    public GameBoardImpl(final List<String> players, int rows, int columns) throws MalformedGameRequestException {
        if(players.size() != TOTAL_PLAYERS || columns != LENGTH || rows != LENGTH) {
            throw new MalformedGameRequestException();
        }
        if(players.get(0).isEmpty() || players.get(1).isEmpty() || players.get(0).equals(players.get(1))) {
            throw new MalformedGameRequestException();
        }
        this.players[0] = players.get(0);
        this.players[1] = players.get(1);
    }

    // useful for testing
    @Override
    public String getStatus() {
        return status.toString();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public int getTotalMoves() {
        return moves.size();
    }

    @Override
    public List<String> getPlayers() {
        return Arrays.asList(players);
    }

    @Override
    public com._98point6.droptoken.dto.game.GameState getGameState() {
        com._98point6.droptoken.dto.game.GameState gameState =
                new com._98point6.droptoken.dto.game.GameState(
                        players, status.toString(), winner
                );
        return gameState;
    }

    @Override
    public String postMove(String playerId, int column) throws PlayerOutOfTurnException, InvalidGameOrPlayerException,
            MalformedInputException, IllegalMoveException {
        if(gameIsOver()) {
            throw new MalformedInputException();
        }
        if(invalidColumn(column)) {
            throw new MalformedInputException();
        }
        if(invalidPlayer(playerId)) {
            throw new InvalidGameOrPlayerException();
        }
        if(!thisPlayersTurn(playerId)) {
            throw new PlayerOutOfTurnException();
        }
        for (int row = 0; row < LENGTH; row++) {
            if(matrix[row][column] == null) {
                matrix[row][column] = playerId;
                recordNextMove(playerId, row, column);
                //"{gameId}/moves/{move_number}"
                return String.format("%s/moves/%d", id, moves.size());
            }
        }

        throw new IllegalMoveException();
    }

    // player can quit any time they want
    @Override
    public void quit(String playerId) {
        if(playerId.equals(players[0])) {
            String winner = players[1];
            setGameWonBy(winner);
            moves.add(Move.quit(winner));
        } else if (playerId.equals(players[1])) {
            String winner = players[0];
            setGameWonBy(winner);
            moves.add(Move.quit(winner));
        }
    }

    @Override
    public List<com._98point6.droptoken.dto.game.Move> getMoves(int from, int to) throws MalformedInputException {
        if (from > to || from >= LENGTH || to >= LENGTH || from < 0 || to < 0) {
            throw new GameBoard.MalformedInputException();
        }
        List<com._98point6.droptoken.dto.game.Move> selectedMoves = new ArrayList<>();

        to = Math.min(to, moves.size() - 1);
        for (int i = from; i <= to ; i++) {

            Move move = moves.get(i);
            com._98point6.droptoken.dto.game.Move moveDto =
                    new com._98point6.droptoken.dto.game.Move(
                            move.getMoveType().toString(),
                            move.getPlayerId(),
                            move.getColumn());

            selectedMoves.add(moveDto);
        }

        return selectedMoves;
    }

    private boolean invalidColumn(int column) {
        return (column < 0 || column >= LENGTH);
    }

    private boolean allSlotsFull() {
        return (moves.size() == LENGTH*LENGTH);
    }

    private boolean gameIsOver() {
        return (winner != null || allSlotsFull());
    }

    private boolean invalidPlayer(String playerId) {
        return (!players[0].equals(playerId) && !players[1].equals(playerId));
    }

    private void setNextPlayer(String currentPlayerId) {
        if(players[0].equals(currentPlayerId)) {
            nextPlayer = players[1];
        } else {
            nextPlayer = players[0];
        }
    }

    private void recordNextMove(String playerId, int row, int column) {
        moves.add(Move.play(playerId, row, column));
        setNextPlayer(playerId);

        // all columns match
        boolean allColumnsMatch = true;
        for (int i = 0; i < LENGTH; i++) {
            if(!StringUtils.equals(matrix[i][column], playerId)) {
                allColumnsMatch = false;
                break;
            }
        }

        if(allColumnsMatch) {
            setGameWonBy(playerId);
            return;
        }

        // all rows match
        boolean allRowsMatch = true;
        for (int i = 0; i < LENGTH; i++) {
            if(!StringUtils.equals(matrix[row][i], playerId)) {
                allRowsMatch = false;
                break;
            }
        }

        if(allRowsMatch) {
            setGameWonBy(playerId);
            return;
        }

        if(allSlotsFull()) {
            setStatus(GameBoardImpl.GameState.DONE);
        }
    }

    private void setGameWonBy(String playerId) {
        winner = playerId;
        status = GameBoardImpl.GameState.DONE;
    }

    private boolean thisPlayersTurn(String playerId) {
        return (nextPlayer == null || nextPlayer.equals(playerId));
    }

    private enum GameState {
        DONE ,
        IN_PROGRESS;

        // Implementing a fromString method on an enum type
        private static final Map<String, GameBoardImpl.GameState> stringToEnum =
                Stream.of(values()).collect(
                        Collectors.toMap(Object::toString, e -> e));

        // Returns State for string, if any
        public static Optional<GameBoardImpl.GameState> fromString(String symbol) {
            return Optional.ofNullable(stringToEnum.get(symbol));
        }
    }

}
