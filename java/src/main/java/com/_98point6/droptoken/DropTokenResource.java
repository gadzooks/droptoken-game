package com._98point6.droptoken;

import com._98point6.droptoken.dto.game.Move;
import com._98point6.droptoken.model.*;
import com._98point6.droptoken.service.DropTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
public class DropTokenResource {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenResource.class);
    private final DropTokenService dropTokenService;

    public DropTokenResource(DropTokenService service) {
        dropTokenService = service;
    }

    @GET
    public Response getGames() {
        GetGamesResponse.Builder builder = new GetGamesResponse.Builder();
        builder.games(dropTokenService.getGames());
        return Response.ok(builder.build()).build();
    }

    @POST
    public Response createNewGame(CreateGameRequest request) {
        logger.info("createNewGame: request={}", request);
        String newGameId = dropTokenService.createGame(request.getPlayers());
        CreateGameResponse.Builder builder = new CreateGameResponse.Builder();
        builder.gameId(newGameId);
        return Response.ok(builder.build()).build();
    }

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) {
        logger.info("getGameStatus: gameId = {}", gameId);

        Optional<GameStatusResponse> gs = dropTokenService.getGameState(gameId);

        if(gs.isPresent()) {
            return Response.ok(gs).build();
        } else {
            return Response.
                    status(Response.Status.NOT_FOUND).
                    build();
        }

    }

    @Path("/{id}/{playerId}")
    @POST
    public Response postMove(@PathParam("id")String gameId, @PathParam("playerId") String playerId, PostMoveRequest request) {
        String moveLink = dropTokenService.nextMove(gameId, playerId, request.getColumn());
        PostMoveResponse.Builder builder = new PostMoveResponse.Builder();
        builder.moveLink(moveLink);
        logger.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        return Response.ok(builder.build()).build();
    }

    @Path("/{id}/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id")String gameId, @PathParam("playerId") String playerId) {
        dropTokenService.quitGame(gameId, playerId);
        logger.info("gameId={}, playerId={}", gameId, playerId);
        return Response.status(202).build();
    }
    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId, @QueryParam("start") Integer start, @QueryParam("until") Integer until) {
        List<Move> results = dropTokenService.getMoves(gameId, start, until);
        List<GetMoveResponse> moves = convertToMoveResponse(results);

        GetMovesResponse.Builder builder = new GetMovesResponse.Builder();
        builder.moves(moves);
        logger.info("gameId={}, start={}, until={}", gameId, start, until);
        return Response.ok(builder.build()).build();
    }

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @PathParam("moveId") Integer moveId) {
        List<Move> results = dropTokenService.getMoves(gameId, moveId, moveId);

        // TODO check if moves == 1
        Move move = results.get(0);

        GetMoveResponse.Builder builder = new GetMoveResponse.Builder();
        builder.player(move.getPlayer());
        if(move.getColumn() >= 0) {
            builder.column(move.getColumn());
        }
        builder.type(move.getMoveType());

        logger.info("gameId={}, moveId={}", gameId, moveId);
        return Response.ok(new GetMoveResponse()).build();
    }

    private List<GetMoveResponse> convertToMoveResponse(List<Move> moves) {
        return moves.stream().
                map(m -> {
                    GetMoveResponse.Builder builder = new GetMoveResponse.Builder().
                            type(m.getMoveType()).
                            player(m.getPlayer());
                    // -1 specifies QUIT move type
                    if(m.getColumn() >= 0) {
                        builder.column(m.getColumn());
                    }
                    return builder.build();
                }).
                collect(Collectors.toList());
    }

}
