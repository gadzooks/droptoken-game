package com._98point6.droptoken.model.game;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {
    private static final int LENGTH = 4;
    private static final int TOTAL_PLAYERS = 2;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final String[] players = new String[TOTAL_PLAYERS];
    private final String[][] matrix = new String[LENGTH][LENGTH];
    private final List<Move> moves = new ArrayList<>();
    private GameState state = GameState.IN_PROGRESS;
    @Getter
    private String winner;

    public GameBoard(final List<String> players) {
        if(players.size() != TOTAL_PLAYERS) {
            throw new IllegalArgumentException(
                    String.format("game needs exactly %d players, but %d were provided", players.size(), TOTAL_PLAYERS));
        }
        this.players[0] = players.get(0);
        this.players[1] = players.get(1);
    }

    public int getTotalMoves() {
        return moves.size();
    }

    public List<String> getPlayers() {
        return Arrays.asList(players);
    }

    public String getGameStatus() {
        return state.toString();
    }

    public String postMove(String playerId, Integer column) {
        //TODO check playerId and column
        //TODO validate game is not over
        for (int row = 0; row < LENGTH; row++) {
            if(matrix[row][column] == null) {
                matrix[row][column] = playerId;
                recordNextMove(playerId, row, column);
                //"{gameId}/moves/{move_number}"
                return String.format("%s/moves/%d", id, moves.size());
            }
        }

        return null;
    }

    private void recordNextMove(String playerId, Integer row, Integer column) {
        moves.add(Move.play(playerId, row, column));

        // all columns match
        boolean allColumnsMatch = true;
        for (int i = 0; i < LENGTH; i++) {
            if(matrix[i][column] != playerId) {
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
            if(matrix[row][i] != playerId) {
                allRowsMatch = false;
                break;
            }
        }

        if(allRowsMatch) {
            setGameWonBy(playerId);
        }
    }

    private void setGameWonBy(String playerId) {
        winner = playerId;
        state = GameState.DONE;
    }

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

    public List<com._98point6.droptoken.dto.game.Move> getMoves(int from, int to) {
        if (from > to || from >= LENGTH || to >= LENGTH) {
            // TODO throw exception ?
            return new ArrayList<>();
        }
        List<com._98point6.droptoken.dto.game.Move> selectedMoves = new ArrayList<>();

        for (int i = from; i <= to ; i++) {

            Move move = moves.get(i);
            int moveColumn = -1;
            com._98point6.droptoken.dto.game.Move moveDto =
                    new com._98point6.droptoken.dto.game.Move(
                            move.getMoveType().toString(),
                            move.getPlayerId(),
                            move.getColumn());

            selectedMoves.add(moveDto);
        }

        return selectedMoves;
    }

    private enum GameState {
        DONE ,
        IN_PROGRESS;

        // Implementing a fromString method on an enum type
        private static final Map<String, GameState> stringToEnum =
                Stream.of(values()).collect(
                        Collectors.toMap(Object::toString, e -> e));

        // Returns State for string, if any
        public static Optional<GameState> fromString(String symbol) {
            return Optional.ofNullable(stringToEnum.get(symbol));
        }
    }


}
