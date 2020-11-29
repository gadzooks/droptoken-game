package com._98point6.droptoken.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Move {
    private final String moveType;
    private final String player;
    private final int column;
}
