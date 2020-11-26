package com._98point6.droptoken.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GameStatus {
    private final List<String> players;
    private final String state;
    private final String winner;
    private final int moves;
}
