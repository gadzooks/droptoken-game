package com._98point6.droptoken.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameState {
    private final String[] players;
    private final String state;
    private final String winner;
}
