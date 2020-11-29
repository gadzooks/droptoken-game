package com._98point6.droptoken.model;

import com._98point6.droptoken.serializer.GameStatusResponseSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

class GameStatusResponseTest {
    private static final SimpleModule MODULE = new SimpleModule().
            addSerializer(GameStatusResponse.class, new GameStatusResponseSerializer(GameStatusResponse.class));
    private static final ObjectMapper MAPPER = new ObjectMapper().
            registerModule(MODULE).
            registerModule(new Jdk8Module());

    @SneakyThrows
    @Test
    public void testWinnerIsExcludedIfNull_ifGameInProgress() {
        GameStatusResponse.Builder builder = new GameStatusResponse.Builder().
                moves(1).
                state("IN_PROGRESS").
                players(new ArrayList<>());

        String dtoAsString = MAPPER.writeValueAsString(builder.build());
        assertThat(dtoAsString, containsString("moves"));
        assertThat(dtoAsString, containsString("state"));
        assertThat(dtoAsString, containsString("players"));
        assertThat(dtoAsString, not(containsString("winner")));
    }

    @SneakyThrows
    @Test
    public void testWinnerIsExcludedIfNull_ifGameIsDone() {
        GameStatusResponse.Builder builder = new GameStatusResponse.Builder().
                moves(1).
                state("DONE").
                players(new ArrayList<>());

        String dtoAsString = MAPPER.writeValueAsString(builder.build());
        assertThat(dtoAsString, containsString("moves"));
        assertThat(dtoAsString, containsString("state"));
        assertThat(dtoAsString, containsString("players"));
        assertThat(dtoAsString, containsString("winner"));
    }

    @SneakyThrows
    @Test
    public void testWinnerIsExcludedIfNull_ifGameHasWinner() {
        GameStatusResponse.Builder builder = new GameStatusResponse.Builder().
                moves(1).
                state("DONE-WITH-WINNER").
                winner("winner").
                players(new ArrayList<>());

        String dtoAsString = MAPPER.writeValueAsString(builder.build());
        assertThat(dtoAsString, containsString("moves"));
        assertThat(dtoAsString, containsString("state"));
        assertThat(dtoAsString, containsString("players"));
        assertThat(dtoAsString, containsString("winner"));
    }

}