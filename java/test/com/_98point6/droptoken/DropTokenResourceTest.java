package com._98point6.droptoken;

import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;
import com._98point6.droptoken.service.DropTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropTokenResourceTest {
    private static final String P1 = "P1";
    private static final String P2 = "P2";
    private static final List<String> PLAYERS = Arrays.asList(P1, P2);
    private static final DropTokenService SERVICE = mock(DropTokenService.class);
    public static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new DropTokenResource(SERVICE))
            .build();
    private GameBoard game;

    @BeforeEach
    public void beforeEach() {
        game = new GameBoardImpl(PLAYERS, 4, 4);
    }

    @Test
    public void testGetGames() {
        String[] games = new String[] {game.getId().toString()};
        when(SERVICE.getGames()).thenReturn(Arrays.asList(games));
        Response response = EXT.target("/drop_token").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusInfo().getStatusCode());
    }

    @Test
    public void testCreateNewGame() throws JsonProcessingException {
        // GIVEN
        var values = new HashMap<String, Object>() {{
            put("players", new String[] {P1, P2});
            put ("rows", "4");
            put ("columns", "4");
        }};

        var objectMapper = new ObjectMapper();
        String json = objectMapper
                .writeValueAsString(values);

        var gameBoard = new GameBoardImpl(PLAYERS, 4, 4);
        when(SERVICE.createGame(PLAYERS, 4, 4)).thenReturn(Optional.of(gameBoard));

        // WHEN
        Response response = EXT.target("/drop_token").request().post(Entity.json(json));

        // THEN
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusInfo().getStatusCode());

    }

    @Test
    public void testGetGameStatus() {
    }

    @Test
    public void testPostMove() {
    }

    @Test
    public void testPlayerQuit() {
    }

    @Test
    public void testGetMoves() {
    }

    @Test
    public void testGetMove() {
    }
}