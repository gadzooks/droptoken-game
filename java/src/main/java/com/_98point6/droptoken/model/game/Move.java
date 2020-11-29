package com._98point6.droptoken.model.game;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Getter
class Move {

    private final MoveType moveType;
    private final String playerId;
    private final int row; //useful for debugging
    private final int column;

    public static Move play(String playerId, int row, int column) {
        return new Move(MoveType.MOVE,playerId,row, column);
    }

    public static Move quit(String playerId) {
        return new Move(MoveType.QUIT, playerId, -1, -1);
    }

    public int getColumn() {
        if(moveType == MoveType.QUIT) {
            return -1;
        } else {
            return column;
        }
    }

    public enum MoveType {
        MOVE {
            @Override
            public boolean validMove(MoveType nextType) {
                // all moves are valid next moves
                return true;
            }
        },
        QUIT{
            @Override
            public boolean validMove(MoveType nextType) {
                // all moves are in-valid next moves
                return false;
            }
        };

        // Implementing a fromString method on an enum type
        private static final Map<String, MoveType> stringToEnum =
                Stream.of(values()).collect(
                        Collectors.toMap(Object::toString, e -> e));

        // Returns State for string, if any
        public static Optional<MoveType> fromString(String symbol) {
            return Optional.ofNullable(stringToEnum.get(symbol));
        }

        abstract boolean validMove(MoveType nextMove);
    }
}
