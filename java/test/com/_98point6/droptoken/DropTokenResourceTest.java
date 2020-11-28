package com._98point6.droptoken;

import com._98point6.droptoken.model.CreateGameResponse;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.PostMoveResponse;
import com._98point6.droptoken.service.DropTokenService;
import com._98point6.droptoken.service.DropTokenServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import lombok.SneakyThrows;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropTokenResourceTest {
    private static final String P1 = "P1";
    private static final String P2 = "P2";
    private static final List<String> PLAYERS = Arrays.asList(P1, P2);
    // Dont need to mock this since its a light weight service and we can test end-to-end functionality that way
    private static final DropTokenService SERVICE = new DropTokenServiceImpl();
    public static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new DropTokenResource(SERVICE))
            .addResource(new DropTokenExceptionMapper())
            .build();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
//        game = new GameBoardImpl(PLAYERS, 4, 4);
    }

    @Test
    public void testGetGames() {
        Response response = EXT.target("/drop_token").request().get();
        assertThat(response.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testGetGameById_withValidInput() {
        // GIVEN
        String json = createGameJson(P1, P2);
        Response response = EXT.target("/drop_token").request().post(Entity.json(json));
        assertThat(response.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());
        CreateGameResponse createGameResponse = response.readEntity(CreateGameResponse.class);

        // WHEN called with an valid id
        Response getStatusResponse = EXT.target(String.format("/drop_token/%s", createGameResponse.getGameId())).
                request(MediaType.APPLICATION_JSON).
                get();

        // THEN response should be 200
        assertThat(getStatusResponse.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());

    }

    @Test
    public void testGetGameById_withBadInput() {
        // GIVEN

        // WHEN called with an invalid id
        Response getStatusResponse = EXT.target(String.format("/drop_token/%s", UUID.randomUUID().toString())).
                request(MediaType.APPLICATION_JSON).
                get();

        // THEN response should be 200
        assertThat(getStatusResponse.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    public void testCreateNewGame_withValidInput() {
        // GIVEN
        String json = createGameJson(P1, P2);

        // WHEN
        Response response = EXT.target("/drop_token").request().post(Entity.json(json));

        // THEN
        assertThat(response.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testCreateNewGame_withBadInput() {
        // GIVEN
        // Both players are the same - invalid input
        String json = createGameJson(P1, P1);

        // WHEN
        Response response = EXT.target("/drop_token").
                request(MediaType.APPLICATION_JSON).
                post(Entity.json(json));

        // THEN
        assertThat(response.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testGetGameStatus_withSimpleGame() {
        // GIVEN 1 valid game
        Response response = EXT.target("/drop_token").
                request(MediaType.APPLICATION_JSON).
                post(Entity.json(createGameJson(P1, P2)));
        CreateGameResponse resp = response.readEntity(CreateGameResponse.class);

        // Do some moves : P1 and the P2 will put tokens in 0,1,2,3. P1 should win
        makeAMove(resp.getGameId(), P1, 0,1);
        checkGameStatus(resp.getGameId(),"IN_PROGRESS", null);
        makeAMove(resp.getGameId(), P2, 0,2);
        checkGameStatus(resp.getGameId(),"IN_PROGRESS", null);
        makeAMove(resp.getGameId(), P1, 1,3);
        checkGameStatus(resp.getGameId(),"IN_PROGRESS", null);
        makeAMove(resp.getGameId(), P2, 1,4);
        checkGameStatus(resp.getGameId(),"IN_PROGRESS", null);
        makeAMove(resp.getGameId(), P1, 2,5);
        checkGameStatus(resp.getGameId(),"IN_PROGRESS", null);
        makeAMove(resp.getGameId(), P2, 2,6);

        // winning move !!
        makeAMove(resp.getGameId(), P1, 3,7);
        checkGameStatus(resp.getGameId(),"DONE", P1);

        // neither player should be able to quit once game is over
        makeAnInvalidMove(resp.getGameId(), P1, 0);
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

    private void checkGameStatus(String gameId, String expectedState, String expectedWinner) {
        Response move = EXT.target(String.format("/drop_token/%s", gameId)).
                request(MediaType.APPLICATION_JSON).
                get();
        assertThat(move.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());

        GameStatusResponse response = move.readEntity(GameStatusResponse.class);
        assertThat(response.getState()).isEqualTo(expectedState);
        if(expectedWinner == null) {
            assertFalse(response.getWinner().isPresent());
        } else {
            String winner = response.getWinner().get();
            assertThat(winner).isEqualTo(expectedWinner);
        }
    }

    private void makeAnInvalidMove(String gameId, String playerId, int column) {
        Response move = EXT.target(String.format("/drop_token/%s/%s", gameId, playerId)).
                request(MediaType.APPLICATION_JSON).
                post(Entity.json(createGameMoveJson(column)));
        assertThat(move.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    private void makeAMove(String gameId, String playerId, int column, int expectedMoveNumber) {
        Response move = EXT.target(String.format("/drop_token/%s/%s", gameId, playerId)).
                request(MediaType.APPLICATION_JSON).
                post(Entity.json(createGameMoveJson(column)));
        assertThat(move.getStatusInfo().getStatusCode()).
                isEqualTo(Response.Status.OK.getStatusCode());

        PostMoveResponse response = move.readEntity(PostMoveResponse.class);
        assertThat(response.getMoveLink()).
                isEqualTo(String.format("%s/moves/%d", gameId, expectedMoveNumber));

    }

    @SneakyThrows
    private String createGameJson(String p1, String p2) {
        var values = new HashMap<String, Object>() {{
            put("players", new String[] {p1, p2});
            put ("rows", "4");
            put ("columns", "4");
        }};

        return OBJECT_MAPPER.writeValueAsString(values);
    }

    @SneakyThrows
    private String createGameMoveJson(int column) {
        var values = new HashMap<String, Object>() {{
            put ("column", column);
        }};

        return OBJECT_MAPPER.writeValueAsString(values);
    }


}